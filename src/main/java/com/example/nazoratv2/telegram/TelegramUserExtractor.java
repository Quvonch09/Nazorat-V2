package com.example.nazoratv2.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TelegramUserExtractor {
    private static final ObjectMapper om = new ObjectMapper();

    public static long extractTelegramId(String initData) {
        var params = com.example.nazoratv2.security.telegram.TelegramInitDataVerifier.parseQuery(initData);
        String userJson = params.get("user");
        if (userJson == null || userJson.isBlank()) {
            throw new RuntimeException("Telegram initData ichida user yo'q");
        }
        try {
            var node = om.readTree(userJson);
            return node.get("id").asLong();
        } catch (Exception e) {
            throw new RuntimeException("Telegram user json parse xato", e);
        }
    }
}