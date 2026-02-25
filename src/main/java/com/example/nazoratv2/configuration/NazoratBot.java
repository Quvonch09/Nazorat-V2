package com.example.nazoratv2.configuration;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.RegisterSession;
import com.example.nazoratv2.dto.request.ReqStudentBot;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.entity.enums.Step;
import com.example.nazoratv2.repository.GroupRepository;
import com.example.nazoratv2.repository.StudentRepository;
import com.example.nazoratv2.repository.UserRepository;
import com.example.nazoratv2.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class NazoratBot extends TelegramLongPollingBot {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
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

        if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }
    }

    // ===================== MESSAGE =====================

    private void handleMessage(Message msg) {

        long chatId = msg.getChatId();
        long tgId = msg.getFrom().getId();

        // ===== commands =====
        if (msg.hasText()) {
            String text = msg.getText().trim();

            if ("/start".equals(text)) {
                sendText(chatId,
                        "Assalomu alaykum!\n\n" +
                                "Ro'yxatdan o'tish: /register\n" +
                                "Bekor qilish: /cancel");
                return;
            }

            if ("/cancel".equals(text)) {
                sessions.remove(tgId);
                sendText(chatId, "Bekor qilindi. Qaytadan /register yozing.");
                return;
            }

            if ("/register".equals(text)) {
                RegisterSession s = new RegisterSession();
                s.setStep(Step.STUDENT_PHONE);
                sessions.put(tgId, s);

                askStudentPhoneWithContactButton(chatId); // ✅ 2-rasmdagi contact tugma
                return;
            }
        }

        RegisterSession s = sessions.get(tgId);
        if (s == null) return;

        switch (s.getStep()) {

            case STUDENT_PHONE -> {

                if (!msg.hasContact()) {
                    sendText(chatId, "Iltimos pastdagi tugma orqali telefoningizni yuboring 👇");
                    askStudentPhoneWithContactButton(chatId);
                    return;
                }

                String phone = normalizePhone(msg.getContact().getPhoneNumber());
                if (!isUzPhone(phone)) {
                    sendText(chatId, "Telefon formati xato. Masalan: 998901234567");
                    askStudentPhoneWithContactButton(chatId);
                    return;
                }

                // ✅ 1) Avval tekshiramiz: bu phone USER jadvalida bormi va ROLE_PARENTmi?
                var parentOpt = userRepository.findByPhoneAndActiveTrue(phone);

                if (parentOpt.isPresent()) {
                    // ✅ bu parent registratsiyasi ekan
                    String username = msg.getFrom().getUserName(); // null bo'lishi mumkin

                    // parent telegramni bog'laymiz (telegramId + username)
                    ApiResponse<String> linkRes = authService.linkParentTelegram(phone, tgId, username);

                    if (!linkRes.isSuccess()) {
                        sendText(chatId, "❌ Xatolik: " + linkRes.getMessage());
                        return;
                    }

                    // contact keyboardni olib tashlaymiz
                    sendTextRemoveKeyboard(chatId, "✅ Parent topildi. Farzandlaringiz ro'yxati:");

                    // ✅ parent bolalarini chiqaramiz (button)
                    showParentChildren(chatId, parentOpt.get().getId());

                    // parent flow bo'lgani uchun sessionni tozalab yuborsak ham bo'ladi,
                    // lekin tanlash callback'iga parentId kerak bo'ladi.
                    // Shuning uchun sessionga parentId saqlab ketamiz:
                    s.setParentPhone(phone);
                    s.setParentId(parentOpt.get().getId()); // RegisterSessionga qo'shasan
                    s.setStep(Step.PARENT_PICK_CHILD);      // Stepga qo'shasan

                    return;
                }

                // ✅ 2) Parent emas => student registr davom etadi
                s.setStudentPhone(phone);
                s.setStudentTelegramId(tgId); // (sessionga saqlab qo'ygan yaxshi)
                s.setStep(Step.STUDENT_NAME);

                sendTextRemoveKeyboard(chatId, "Ism-familiyangizni kiriting:");
            }

            case STUDENT_NAME -> {
                if (!msg.hasText() || msg.getText().trim().isEmpty()) {
                    sendText(chatId, "Iltimos ism-familiyangizni yozing.");
                    return;
                }
                s.setStudentName(msg.getText().trim());
                s.setStep(Step.SELECT_GROUP);
                sendGroupsKeyboard(chatId);
            }

            case PARENT_CONTACT -> {
                if (!msg.hasText()) {
                    sendText(chatId, "Ota-onangizning telegram username (@username) yoki telefonini (998...) kiriting.");
                    return;
                }

                String input = msg.getText().trim();

                // username
                if (input.startsWith("@")) {
                    String username = input.substring(1).trim();
                    if (username.isEmpty()) {
                        sendText(chatId, "Username xato. Masalan: @username");
                        return;
                    }

                    s.setParentUsername(username);

                    boolean exists = userRepository.existsByTelegramUsername(username);
                    if (exists) {
                        completeRegistration(chatId, s, tgId);
                    } else {
                        s.setStep(Step.PARENT_PHONE);
                        askParentPhoneAsText(chatId); // ✅ 1-rasmdagi oddiy text
                    }
                    return;
                }

                // phone
                String parentPhone = normalizePhone(input);
                if (!isUzPhone(parentPhone)) {
                    sendText(chatId, "Telefon formati xato. Masalan: 998901234567");
                    return;
                }

                s.setParentPhone(parentPhone);

                boolean exists = userRepository.existsByPhoneAndActiveTrue(parentPhone);
                if (exists) {
                    completeRegistration(chatId, s, tgId);
                } else {
                    s.setStep(Step.PARENT_NAME);
                    sendText(chatId, "Ota-onangizning ism-familiyasini kiriting:");
                }
            }

            case PARENT_PHONE -> {
                if (!msg.hasText()) {
                    askParentPhoneAsText(chatId);
                    return;
                }

                String parentPhone = normalizePhone(msg.getText());
                if (!isUzPhone(parentPhone)) {
                    sendText(chatId, "Telefon formati xato. Masalan: 998901234567");
                    return;
                }

                s.setParentPhone(parentPhone);

                // endi phone bo'yicha topilsa -> register
                boolean exists = userRepository.existsByPhoneAndActiveTrue(parentPhone);
                if (exists) {
                    completeRegistration(chatId, s, tgId);
                } else {
                    s.setStep(Step.PARENT_NAME);
                    sendText(chatId, "Ota-onangizning ism-familiyasini kiriting:");
                }
            }

            case PARENT_NAME -> {
                if (!msg.hasText() || msg.getText().trim().isEmpty()) {
                    sendText(chatId, "Iltimos ota-onangizning ism-familiyasini yozing.");
                    return;
                }
                s.setParentName(msg.getText().trim());
                completeRegistration(chatId, s, tgId);
            }

            default -> sendText(chatId, "Jarayon noma'lum holatga tushib qoldi. /register dan qayta boshlang.");
        }
    }

    // ===================== CALLBACK =====================

    private void handleCallback(CallbackQuery cb) {

        long tgId = cb.getFrom().getId();
        long chatId = cb.getMessage().getChatId();

        RegisterSession s = sessions.get(tgId);
        if (s == null) return;

        String data = cb.getData();

        if (data != null && data.startsWith("GROUP:")) {
            Long groupId = parseLongSafe(data.substring(6));
            if (groupId == null) {
                sendText(chatId, "Guruh tanlashda xatolik.");
                return;
            }

            s.setGroupId(groupId);
            s.setStep(Step.PARENT_CONTACT);

            sendText(chatId, "Ota-onangizning telegram username (@username) yoki telefonini (998...) kiriting:");
        }

        if (data.startsWith("PICK_CHILD:")) {
            Long studentId = parseLongSafe(data.substring("PICK_CHILD:".length()));
            if (studentId == null) {
                sendText(chatId, "Xatolik: student topilmadi.");
                return;
            }

            s.setPickedStudentId(studentId);
            s.setStep(Step.PARENT_CONFIRM_CHILD);

            sendParentConfirm(chatId, studentId);
            return;
        }

        if (data.startsWith("PARENT_CONFIRM:")) {
            // format: PARENT_CONFIRM:YES:123  yoki  PARENT_CONFIRM:NO
            String[] parts = data.split(":");
            String yesNo = parts.length > 1 ? parts[1] : "NO";

            if ("NO".equalsIgnoreCase(yesNo)) {
                sessions.remove(tgId);
                sendText(chatId, "Bekor qilindi.");
                return;
            }

            Long studentId = (parts.length > 2) ? parseLongSafe(parts[2]) : s.getPickedStudentId();
            if (studentId == null) {
                sendText(chatId, "Xatolik: student tanlanmagan.");
                return;
            }

            // ✅ Parent login/parol chiqaramiz + mini app
            String parentPhone = s.getParentPhone();
            sendText(chatId,
                    "✅ Tasdiqlandi!\n\n" +
                            "👨‍👩‍👧 Parent login:\n" +
                            "Login: " + parentPhone + "\n" +
                            "Password: " + last4(parentPhone));

            sendMiniAppButton(chatId);

            sessions.remove(tgId);
            return;
        }
    }

    // ===================== COMPLETE =====================

    private void completeRegistration(long chatId, RegisterSession s, long studentTgId) {

        ReqStudentBot req = new ReqStudentBot();
        req.setFullName(s.getStudentName());
        req.setPhone(s.getStudentPhone());
        req.setGroupId(s.getGroupId());
        req.setStudentTelegramId(studentTgId);

        req.setParentUsername(s.getParentUsername());
        req.setParentPhone(s.getParentPhone());
        req.setParentName(s.getParentName());

        ApiResponse<String> res;
        try {
            res = authService.registerFromTelegram(req);
        } catch (Exception e) {
            e.printStackTrace();
            sendText(chatId, "❌ Server xatosi: " + e.getMessage());
            return;
        }

        if (!res.isSuccess()) {
            sendText(chatId, "❌ Xatolik: " + res.getMessage());
            return;
        }

        sendText(chatId,
                "✅ Arizangiz qabul qilindi!\n" +
                        "⏳ Admin tasdiqlashini kuting.\n\n" +
                        "Tasdiqlangandan keyin login/parol va Mini App shu botdan yuboriladi.");

//        sendMiniAppButton(chatId);
        sessions.remove(studentTgId);
    }

    // ===================== KEYBOARDS =====================

    private void sendGroupsKeyboard(long chatId) {

        List<Group> groups = groupRepository.findAll();
        if (groups.isEmpty()) {
            sendText(chatId, "Hozircha guruhlar topilmadi.");
            return;
        }

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

    private void sendMiniAppButton(long chatId) {

        InlineKeyboardButton btn = InlineKeyboardButton.builder()
                .text("📲 Mini App ni ochish")
                .webApp(new WebAppInfo(webAppUrl))
                .build();

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(btn)))
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

    // ✅ Student phone uchun contact tugma (2-rasm)
    private void askStudentPhoneWithContactButton(long chatId) {
        KeyboardButton contactBtn = new KeyboardButton("📱 Kontaktni yuborish");
        contactBtn.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(contactBtn);

        ReplyKeyboardMarkup kb = new ReplyKeyboardMarkup();
        kb.setResizeKeyboard(true);
        kb.setOneTimeKeyboard(true);
        kb.setSelective(true);
        kb.setKeyboard(List.of(row));

        SendMessage sm = SendMessage.builder()
                .chatId(chatId)
                .text("Telefon raqamingizni yuboring 👇")
                .replyMarkup(kb)
                .build();

        try { execute(sm); } catch (Exception e) { e.printStackTrace(); }
    }

    // ✅ Parent phone text qilib (1-rasm)
    private void askParentPhoneAsText(long chatId) {
        ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);

        SendMessage sm = SendMessage.builder()
                .chatId(chatId)
                .text("Ota-onangizning telefon raqamini yozib yuboring (998XXXXXXXXX):")
                .replyMarkup(remove)
                .build();

        try { execute(sm); } catch (Exception e) { e.printStackTrace(); }
    }

    // ===================== HELPERS =====================

    private void sendText(long chatId, String text) {
        try {
            execute(SendMessage.builder().chatId(chatId).text(text).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // contact tugmani olib tashlab text yuborish
    private void sendTextRemoveKeyboard(long chatId, String text) {
        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .replyMarkup(new ReplyKeyboardRemove(true))
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isUzPhone(String phone) {
        return phone != null && phone.matches("^998\\d{9}$");
    }

    private String normalizePhone(String s) {
        if (s == null) return null;
        return s.replace("+", "").replace(" ", "").trim();
    }

    private Long parseLongSafe(String s) {
        try { return Long.parseLong(s); } catch (Exception e) { return null; }
    }

    private String last4(String phone) {
        if (phone == null || phone.length() < 4) return "";
        return phone.substring(phone.length() - 4);
    }


    private void showParentChildren(long chatId, Long parentId) {

        var kids = studentRepository.findAllByParent_Id(parentId);

        if (kids == null || kids.isEmpty()) {
            sendText(chatId, "Hozircha sizga biriktirilgan farzand topilmadi.");
            return;
        }

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (var st : kids) {
            rows.add(List.of(
                    InlineKeyboardButton.builder()
                            .text(st.getFullName() + " (" + st.getPhone() + ")")
                            .callbackData("PICK_CHILD:" + st.getId())
                            .build()
            ));
        }

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();

        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text("Farzandingizni tanlang 👇")
                    .replyMarkup(markup)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendParentConfirm(long chatId, Long studentId) {

        InlineKeyboardButton yes = InlineKeyboardButton.builder()
                .text("✅ Tasdiqlayman")
                .callbackData("PARENT_CONFIRM:YES:" + studentId)
                .build();

        InlineKeyboardButton no = InlineKeyboardButton.builder()
                .text("❌ Bekor qilish")
                .callbackData("PARENT_CONFIRM:NO")
                .build();

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(yes, no)))
                .build();

        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text("Shu farzand sizniki ekanligini tasdiqlaysizmi?")
                    .replyMarkup(markup)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}