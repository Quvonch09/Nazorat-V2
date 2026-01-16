package com.example.nazoratv2.controller;

import com.example.nazoratv2.configuration.TrackAction;
import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.Token;
import com.example.nazoratv2.entity.enums.ActionType;
import com.example.nazoratv2.security.JwtService;
import com.example.nazoratv2.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @TrackAction(
            type = ActionType.LOGIN,
            description = "Login qilindi"
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> adminLogin(
            @RequestParam String phone,
            @RequestParam String password
    ){
        return ResponseEntity.ok(authService.login(phone, password));
    }


    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> checkToken(@RequestBody Token token){
        return ResponseEntity.ok(authService.validate(token));
    }

}
