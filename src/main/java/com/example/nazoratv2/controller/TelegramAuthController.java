package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TelegramAuthController {

    private final AuthService telegramAuthService;

    @PostMapping("/telegram")
    public ApiResponse<?> auth(@RequestBody TelegramAuthRequest req) {
        var result = telegramAuthService.login(req.initData());
        return ApiResponse.success(result, "OK");
    }

    public record TelegramAuthRequest(String initData) {}
}