package com.example.nazoratv2.controller;

import com.example.nazoratv2.configuration.TrackAction;
import com.example.nazoratv2.entity.enums.ActionType;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.AuthRegister;
import com.example.nazoratv2.dto.request.ReqStudent;
import com.example.nazoratv2.entity.enums.Role;
import com.example.nazoratv2.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {
    private final AuthService authService;

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

}
