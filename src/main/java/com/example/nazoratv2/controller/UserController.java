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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

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

    @GetMapping
    @Operation(description = "ROLE_TEACHER , ROLE_ADMIN , ROLE_PARENT")
    public ResponseEntity<ApiResponse<ResPageable>> getAllUsersPage(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(required = false) Role role,
                                                                    @RequestParam(required = false) String name,
                                                                    @RequestParam(required = false) String phone){
        return ResponseEntity.ok(userService.getAllUsersSearch(name,phone,role,page,size));
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getProfile(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return userService.getProfile(currentUser);
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
