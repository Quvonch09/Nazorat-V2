package com.example.nazoratv2.configuration;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.RegisterSession;
import com.example.nazoratv2.dto.request.ReqStudentBot;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.entity.enums.Step;
import com.example.nazoratv2.repository.GroupRepository;
import com.example.nazoratv2.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class NazoratBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    private final AuthService authService;
    private final GroupRepository groupRepository;

    // telegramUserId -> session
    private final Map<Long, RegisterSession> sessions = new ConcurrentHashMap<>();

    // ====== Telegram required ======
    @Override public String getBotUsername() { return botUsername; }
    @Override public String getBotToken() { return botToken; }

    // ====== Main Update Handler ======
    @Override
    public void onUpdateReceived(Update update) {

        // 1) Callback tugmalar (GROUP tanlash, CONFIRM)
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
            return;
        }

        // 2) Oddiy text message
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage());
        }
    }

    // ====== Message Handler ======
    private void handleMessage(Message msg) {
        long chatId = msg.getChatId();
        long tgId = msg.getFrom().getId();
        String text = msg.getText().trim();

        if ("/start".equals(text)) {
            sendText(chatId, "Assalomu alaykum! Ro'yxatdan o'tish uchun /register yozing.");
            return;
        }

        if ("/register".equals(text)) {
            RegisterSession s = new RegisterSession();
            s.setStep(Step.PARENT_NAME);
            sessions.put(tgId, s);
            sendText(chatId, "Ota-onaning ismini kiriting:");
            return;
        }

        if ("/cancel".equals(text)) {
            sessions.remove(tgId);
            sendText(chatId, "Bekor qilindi. Qaytadan /register yozing.");
            return;
        }

        RegisterSession s = sessions.get(tgId);
        if (s == null) {
            sendText(chatId, "Buyruqni boshlash uchun /register yozing. Bekor qilish: /cancel");
            return;
        }

        switch (s.getStep()) {
            case PARENT_NAME -> {
                s.setParentName(text);
                s.setStep(Step.PARENT_PHONE);
                sendText(chatId, "Ota-onaning telefon raqamini kiriting (masalan: 998901234567):");
            }

            case PARENT_PHONE -> {
                if (!isUzPhone(text)) {
                    sendText(chatId, "Telefon formati xato. Masalan: 998901234567");
                    return;
                }
                s.setParentPhone(text);
                s.setStep(Step.STUDENT_NAME);
                sendText(chatId, "Studentning ism-familiyasini kiriting:");
            }

            case STUDENT_NAME -> {
                s.setStudentName(text);
                s.setStep(Step.STUDENT_PHONE);
                sendText(chatId, "Student telefonini kiriting (998XXXXXXXXX):");
            }

            case STUDENT_PHONE -> {
                if (!isUzPhone(text)) {
                    sendText(chatId, "Telefon formati xato. Masalan: 998901234567");
                    return;
                }
                s.setStudentPhone(text);

                // ✅ endi GROUP_ID so'ramaymiz, button chiqaramiz
                s.setStep(Step.SELECT_GROUP);
                sendGroupsKeyboard(chatId);
            }

            case SELECT_GROUP -> {
                // SELECT_GROUP bosqichida user text yozsa:
                sendText(chatId, "Iltimos guruhni pastdagi tugmalardan tanlang 👇");
                sendGroupsKeyboard(chatId);
            }

            case STUDENT_PASSWORD -> {
                s.setStudentPassword(text);
                s.setStep(Step.CONFIRM);

                // ✅ endi confirm ham button bilan
                sendConfirmKeyboard(chatId, s);
            }

            case CONFIRM -> {
                // CONFIRM bosqichida user text yozsa:
                sendText(chatId, "Tasdiqlash uchun tugmalardan foydalaning 👇");
                sendConfirmKeyboard(chatId, s);
            }
        }
    }

    // ====== Callback Handler ======
    private void handleCallback(CallbackQuery cb) {
        long tgId = cb.getFrom().getId();
        Message msg = (Message) cb.getMessage();
        long chatId = msg.getChatId();
        String data = cb.getData();

        RegisterSession s = sessions.get(tgId);
        if (s == null) {
            answerCallback(cb.getId(), "Session topilmadi. /register dan boshlang.");
            sendText(chatId, "Session topilmadi. /register yozing.");
            return;
        }

        // GROUP tanlash
        if (data != null && data.startsWith("GROUP:")) {
            if (s.getStep() != Step.SELECT_GROUP) {
                answerCallback(cb.getId(), "Hozir group tanlash bosqichi emas.");
                return;
            }

            Long groupId = parseLongSafe(data.substring("GROUP:".length()));
            if (groupId == null) {
                answerCallback(cb.getId(), "Xato group.");
                return;
            }

            // group mavjudligini tekshiramiz
            Optional<Group> gOpt = groupRepository.findById(groupId);
            if (gOpt.isEmpty()) {
                answerCallback(cb.getId(), "Guruh topilmadi.");
                return;
            }

            s.setGroupId(groupId);
            s.setStep(Step.STUDENT_PASSWORD);

            answerCallback(cb.getId(), "Tanlandi: " + gOpt.get().getName());
            sendText(chatId, "Tanlangan guruh: " + gOpt.get().getName() + "\nStudent parolini kiriting (yoki 1234):");
            return;
        }

        // CONFIRM
        if (data != null && data.startsWith("CONFIRM:")) {
            if (s.getStep() != Step.CONFIRM) {
                answerCallback(cb.getId(), "Hozir tasdiqlash bosqichi emas.");
                return;
            }

            String choice = data.substring("CONFIRM:".length());

            if ("NO".equals(choice)) {
                sessions.remove(tgId);
                answerCallback(cb.getId(), "Bekor qilindi");
                sendText(chatId, "Bekor qilindi. Qaytadan /register yozing.");
                return;
            }

            if (!"YES".equals(choice)) {
                answerCallback(cb.getId(), "Noto'g'ri tanlov");
                return;
            }

            // ✅ register
            ReqStudentBot req = new ReqStudentBot();
            req.setParentName(s.getParentName());
            req.setParentPhone(s.getParentPhone());
            req.setFullName(s.getStudentName());
            req.setPhone(s.getStudentPhone());
            req.setPassword(s.getStudentPassword());
            req.setGroupId(s.getGroupId());
            req.setParentTelegramId(tgId);

            ApiResponse<String> res;
            try {
                res = authService.registerFromTelegram(req);
            } catch (Exception e) {
                e.printStackTrace();
                answerCallback(cb.getId(), "Server xatosi");
                sendText(chatId, "❌ Server xatosi: " + e.getMessage());
                return;
            }

            if (res.isSuccess()) {
                answerCallback(cb.getId(), "OK");
                sendText(chatId, "✅ Muvaffaqiyatli ro'yxatdan o'tdingiz! Endi Mini App’dan foydalanishingiz mumkin.");
            } else {
                answerCallback(cb.getId(), "Xatolik");
                sendText(chatId, "❌ Xatolik: " + res.getMessage());
            }

            sessions.remove(tgId);
            return;
        }

        answerCallback(cb.getId(), "Noma'lum amal");
    }

    // ====== Keyboards ======

    private void sendGroupsKeyboard(long chatId) {
        List<Group> groups = groupRepository.findAll(); // xohlasa: active/orderByName

        if (groups.isEmpty()) {
            sendText(chatId, "Hozircha guruhlar topilmadi. Admin bilan bog'laning.");
            return;
        }

        // 2 tadan button qilib chiqaramiz
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (Group g : groups) {
            InlineKeyboardButton btn = InlineKeyboardButton.builder()
                    .text(g.getName())
                    .callbackData("GROUP:" + g.getId())
                    .build();

            row.add(btn);
            if (row.size() == 2) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }
        if (!row.isEmpty()) rows.add(row);

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();

        SendMessage sm = SendMessage.builder()
                .chatId(chatId)
                .text("Guruhni tanlang 👇")
                .replyMarkup(markup)
                .build();

        try {
            execute(sm);
        } catch (Exception e) {
            e.printStackTrace();
            sendText(chatId, "❌ Guruhlarni chiqarishda xatolik: " + e.getMessage());
        }
    }

    private void sendConfirmKeyboard(long chatId, RegisterSession s) {
        String summary =
                "Tekshiring:\n" +
                        "Parent: " + safe(s.getParentName()) + " / " + safe(s.getParentPhone()) + "\n" +
                        "Student: " + safe(s.getStudentName()) + " / " + safe(s.getStudentPhone()) + "\n" +
                        "Group ID: " + s.getGroupId() + "\n\n" +
                        "Tasdiqlaysizmi?";

        InlineKeyboardButton yes = InlineKeyboardButton.builder()
                .text("✅ HA")
                .callbackData("CONFIRM:YES")
                .build();

        InlineKeyboardButton no = InlineKeyboardButton.builder()
                .text("❌ YO'Q")
                .callbackData("CONFIRM:NO")
                .build();

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(yes, no)))
                .build();

        SendMessage sm = SendMessage.builder()
                .chatId(chatId)
                .text(summary)
                .replyMarkup(markup)
                .build();

        try {
            execute(sm);
        } catch (Exception e) {
            e.printStackTrace();
            sendText(chatId, "❌ Tasdiqlashni chiqarishda xatolik: " + e.getMessage());
        }
    }

    // ====== Helpers ======

    private void sendText(long chatId, String text) {
        try {
            execute(SendMessage.builder().chatId(chatId).text(text).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void answerCallback(String callbackQueryId, String text) {
        try {
            execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQueryId)
                    .text(text)
                    .showAlert(false)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isUzPhone(String phone) {
        return phone != null && phone.matches("^998\\d{9}$");
    }

    private Long parseLongSafe(String s) {
        try { return Long.parseLong(s); } catch (Exception e) { return null; }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}