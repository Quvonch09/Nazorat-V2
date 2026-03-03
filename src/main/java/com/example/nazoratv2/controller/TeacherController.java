package com.example.nazoratv2.controller;

import com.example.nazoratv2.configuration.TrackAction;
import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.UserDTO;
import com.example.nazoratv2.dto.request.AuthRegister;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.dto.response.ResTask;
import com.example.nazoratv2.dto.response.ResTeacher;
import com.example.nazoratv2.dto.response.UserResponse;
import com.example.nazoratv2.entity.enums.ActionType;
import com.example.nazoratv2.entity.enums.Role;
import com.example.nazoratv2.security.CustomUserDetails;
import com.example.nazoratv2.service.AuthService;
import com.example.nazoratv2.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.CustomAutowireConfigurer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {
    private final AuthService authService;
    private final UserService userService;

    @TrackAction(
            type = ActionType.TEACHER_CREATED,
            description = "O'qituvchi yaratildi"
    )
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    @PostMapping("/saveUser")
    public ResponseEntity<ApiResponse<String>> userLogin(
            @Valid @RequestBody AuthRegister register
    ){
        return ResponseEntity.ok(authService.saveUser(register, Role.ROLE_TEACHER));
    }


    @GetMapping("/{teacherId}")
    @Operation(summary = "Teacherlarni bittasini kurish")
    public ResponseEntity<ApiResponse<ResTeacher>> getOneTeacher(@PathVariable Long teacherId){
        return ResponseEntity.ok(userService.getOneTeacher(teacherId));
    }


    @PutMapping
    public ResponseEntity<ApiResponse<String>> teacherUpdate(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                             @RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.update(userDetails,userDTO));
    }


    @DeleteMapping("/{teacherId}")
    public ResponseEntity<ApiResponse<String>> deleteTeacher(@PathVariable Long teacherId){
        return ResponseEntity.ok(userService.deleteById(teacherId));
    }


    @GetMapping
    @Operation(summary = "Teacherlarni filtr qilish PAGE")
    public ResponseEntity<ApiResponse<ResPageable>> getAllTeachers(@RequestParam(required = false) String name,
                                                                   @RequestParam(required = false) String phone,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(userService.getAllUsersSearch(name, phone, Role.ROLE_TEACHER,page, size));
    }


    @GetMapping("/list")
    @Operation(summary = "Teacherlarni listda kurish")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllList(){
        return ResponseEntity.ok(userService.getAllList(Role.ROLE_TEACHER));
    }
}
