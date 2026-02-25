package com.example.nazoratv2.service;

import com.example.nazoratv2.configuration.NazoratBot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramNotifyService {

    private final NazoratBot bot;

    @Value("${telegram.webapp.url}")
    private String webAppUrl;

    public void sendText(Long telegramId, String text) {
        if (telegramId == null) return;
        try {
            bot.execute(SendMessage.builder()
                    .chatId(telegramId.toString())
                    .text(text)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMiniApp(Long telegramId) {
        if (telegramId == null) return;

        InlineKeyboardButton btn = InlineKeyboardButton.builder()
                .text("📲 Mini App ni ochish")
                .webApp(new WebAppInfo(webAppUrl))
                .build();

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(btn)))
                .build();

        try {
            bot.execute(SendMessage.builder()
                    .chatId(telegramId.toString())
                    .text("Mini App’ni oching 👇")
                    .replyMarkup(markup)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}