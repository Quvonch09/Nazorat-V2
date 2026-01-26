package com.example.nazoratv2.controller;

import com.example.nazoratv2.configuration.TrackAction;
import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.UserDTO;
import com.example.nazoratv2.dto.request.AuthRegister;
import com.example.nazoratv2.dto.response.UserResponse;
import com.example.nazoratv2.entity.enums.ActionType;
import com.example.nazoratv2.entity.enums.Role;
import com.example.nazoratv2.security.CustomUserDetails;
import com.example.nazoratv2.service.AuthService;
import com.example.nazoratv2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AuthService authService;
    private final UserService userService;

    @TrackAction(
            type = ActionType.ADMIN_CREATED,
            description = "Admin yaratildi"
    )
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @PostMapping("/saveUser")
    public ResponseEntity<ApiResponse<String>> userLogin(
            @RequestBody AuthRegister register
    ){
        return ResponseEntity.ok(authService.saveUser(register, Role.ROLE_ADMIN));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getOneUser(userId));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<String>> updateUser(@AuthenticationPrincipal CustomUserDetails current,
                                                          @RequestBody UserDTO req){
        return ResponseEntity.ok(userService.update(current, req));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long userId){
        return ResponseEntity.ok(userService.deleteById(userId));
    }
}
