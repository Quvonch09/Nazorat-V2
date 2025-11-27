package com.example.nazoratv2.controller;

import lombok.RequiredArgsConstructor;
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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> adminLogin(
            @RequestParam String phone,
            @RequestParam String password
    ){
        return ResponseEntity.ok(authService.login(phone, password));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/saveUser")
    public ResponseEntity<ApiResponse<String>> userLogin(
            @RequestParam Role role,
            @RequestBody AuthRegister register
    ){
        return ResponseEntity.ok(authService.saveUser(register, role));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/saveStudent")
    public ResponseEntity<ApiResponse<String>> studentLogin(@RequestBody ReqStudent reqStudent){
        return ResponseEntity.ok(authService.saveStudent(reqStudent));
    }
}
