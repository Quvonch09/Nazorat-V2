package com.example.nazoratv2.controller;

import com.example.nazoratv2.configuration.TrackAction;
import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.UserDTO;
import com.example.nazoratv2.dto.request.AuthRegister;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.dto.response.UserResponse;
import com.example.nazoratv2.entity.enums.ActionType;
import com.example.nazoratv2.entity.enums.Role;
import com.example.nazoratv2.security.CustomUserDetails;
import com.example.nazoratv2.service.AuthService;
import com.example.nazoratv2.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @Valid @RequestBody AuthRegister register
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


    @GetMapping("/list")
    @Operation(summary = "Adminlarning LIST da malumoti")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllList(){
        return ResponseEntity.ok(userService.getAllList(Role.ROLE_ADMIN));
    }


    @GetMapping
    @Operation(summary = "Adminlarni FILTR qilish PAGE da")
    public ResponseEntity<ApiResponse<ResPageable>> getAll(@RequestParam(required = false) String name,
                                              @RequestParam(required = false) String phone,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(userService.getAllUsersSearch(name, phone, Role.ROLE_ADMIN,page,size));
    }
}
