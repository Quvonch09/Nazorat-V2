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
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class NazoratBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.webapp.url}")
    private String webAppUrl;

    private final AuthService authService;
    private final GroupRepository groupRepository;

    private final Map<Long, RegisterSession> sessions = new ConcurrentHashMap<>();

    @Override public String getBotUsername() { return botUsername; }
    @Override public String getBotToken() { return botToken; }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage());
        }
    }

    private void handleMessage(Message msg) {
        long chatId = msg.getChatId();
        long tgId = msg.getFrom().getId();
        String text = msg.getText().trim();

        if ("/start".equals(text)) {
            sendText(chatId,
                    "Assalomu alaykum!\n\n" +
                            "Ro'yxatdan o'tish uchun: /register\n" +
                            "Bekor qilish: /cancel");
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
                sendText(chatId, "Ota-onaning telefon raqamini kiriting (998XXXXXXXXX):");
            }

            case PARENT_PHONE -> {
                if (!isUzPhone(text)) {
                    sendText(chatId, "Telefon formati xato.");
                    return;
                }
                s.setParentPhone(text);
                s.setStep(Step.STUDENT_NAME);
                sendText(chatId, "Farzandingizning ism-familiyasini kiriting:");
            }

            case STUDENT_NAME -> {
                s.setStudentName(text);
                s.setStep(Step.STUDENT_PHONE);
                sendText(chatId, "Farzandingizning telefon raqamini kiriting (998XXXXXXXXX):");
            }

            case STUDENT_PHONE -> {
                if (!isUzPhone(text)) {
                    sendText(chatId, "Telefon formati xato.");
                    return;
                }
                s.setStudentPhone(text);
                s.setStep(Step.SELECT_GROUP);
                sendGroupsKeyboard(chatId);
            }

            case SELECT_GROUP -> {
                sendText(chatId, "Iltimos guruhni tugmalardan tanlang 👇");
                sendGroupsKeyboard(chatId);
            }

            case CONFIRM -> {
                sendConfirmKeyboard(chatId, s);
            }
        }
    }

    private void handleCallback(CallbackQuery cb) {
        long tgId = cb.getFrom().getId();
        long chatId = cb.getMessage().getChatId();
        String data = cb.getData();

        RegisterSession s = sessions.get(tgId);
        if (s == null) {
            sendText(chatId, "Session topilmadi. /register yozing.");
            return;
        }

        if (data.startsWith("GROUP:")) {

            Long groupId = parseLongSafe(data.substring(6));
            Optional<Group> gOpt = groupRepository.findById(groupId);
            if (gOpt.isEmpty()) {
                sendText(chatId, "Guruh topilmadi.");
                return;
            }

            s.setGroupId(groupId);
            s.setStep(Step.CONFIRM);

            sendConfirmKeyboard(chatId, s);
            return;
        }

        if (data.startsWith("CONFIRM:")) {

            String choice = data.substring(8);

            if ("NO".equals(choice)) {
                sessions.remove(tgId);
                sendText(chatId, "Bekor qilindi.");
                return;
            }

            // REGISTER
            ReqStudentBot req = new ReqStudentBot();
            req.setParentName(s.getParentName());
            req.setParentPhone(s.getParentPhone());
            req.setFullName(s.getStudentName());
            req.setPhone(s.getStudentPhone());
            req.setGroupId(s.getGroupId());
            req.setParentTelegramId(tgId);

            ApiResponse<String> res = authService.registerFromTelegram(req);

            if (res.isSuccess()) {

                String parentPassword = last4(req.getParentPhone());
                String studentPassword = last4(req.getPhone());

                String creds =
                        "✅ Ro'yxatdan o'tdingiz!\n\n" +
                                "👨‍👩‍👧 Parent:\n" +
                                "Login: " + req.getParentPhone() + "\n" +
                                "Password: " + parentPassword + "\n\n" +
                                "🧑‍🎓 Student:\n" +
                                "Login: " + req.getPhone() + "\n" +
                                "Password: " + studentPassword + "\n\n" +
                                "⚠️ Parol — telefonning oxirgi 4 raqami.";

                sendText(chatId, creds);
                sendMiniAppButton(chatId);

            } else {
                sendText(chatId, "❌ Xatolik: " + res.getMessage());
            }

            sessions.remove(tgId);
        }
    }

    private void sendGroupsKeyboard(long chatId) {
        List<Group> groups = groupRepository.findAll();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Group g : groups) {
            rows.add(List.of(
                    InlineKeyboardButton.builder()
                            .text(g.getName())
                            .callbackData("GROUP:" + g.getId())
                            .build()
            ));
        }

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();

        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text("Guruhni tanlang 👇")
                    .replyMarkup(markup)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendConfirmKeyboard(long chatId, RegisterSession s) {

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text("✅ HA")
                                        .callbackData("CONFIRM:YES")
                                        .build(),
                                InlineKeyboardButton.builder()
                                        .text("❌ YO'Q")
                                        .callbackData("CONFIRM:NO")
                                        .build()
                        )
                ))
                .build();

        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text("Ma'lumotlar to'g'rimi?")
                    .replyMarkup(markup)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMiniAppButton(long chatId) {
        InlineKeyboardButton openApp = InlineKeyboardButton.builder()
                .text("📲 Mini App ni ochish")
                .webApp(new WebAppInfo(webAppUrl))
                .build();

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(openApp)))
                .build();

        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text("Mini App’ni oching 👇")
                    .replyMarkup(markup)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendText(long chatId, String text) {
        try {
            execute(SendMessage.builder().chatId(chatId).text(text).build());
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

    private String last4(String phone) {
        return phone.substring(phone.length() - 4);
    }
}