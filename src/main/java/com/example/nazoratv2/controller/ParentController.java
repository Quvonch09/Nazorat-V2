package com.example.nazoratv2.controller;

import com.example.nazoratv2.configuration.TrackAction;
import com.example.nazoratv2.dto.*;
import com.example.nazoratv2.dto.request.AuthRegister;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.dto.response.ResUser;
import com.example.nazoratv2.dto.response.UserResponse;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.entity.enums.ActionType;
import com.example.nazoratv2.entity.enums.Role;
import com.example.nazoratv2.security.CustomUserDetails;
import com.example.nazoratv2.service.AuthService;
import com.example.nazoratv2.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/parent")
@RequiredArgsConstructor
public class ParentController {

    private final AuthService authService;
    private final UserService userService;

    @TrackAction(
            type = ActionType.PARENT_CREATED,
            description = "Ota-ona yaratildi"
    )
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_PARENT')")
    @PostMapping
    public ResponseEntity<ApiResponse<String>> parentLogin(
            @Valid @RequestBody AuthRegister register
    ){
        return ResponseEntity.ok(authService.saveUser(register, Role.ROLE_PARENT));
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_PARENT')")
    @PutMapping
    public ResponseEntity<ApiResponse<String>> updateParent(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                            @Valid @RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.update(customUserDetails, userDTO));
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_PARENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteParent(@PathVariable Long id){
        return ResponseEntity.ok(userService.deleteById(id));
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_PARENT')")
    @GetMapping
    public ResponseEntity<ApiResponse<ResPageable>> getParent(@RequestParam(required = false) String name,
                                                              @RequestParam(required = false) String phone,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(userService.getAllUsersSearch(name,phone,Role.ROLE_PARENT,page,size));
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_PARENT')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getParentById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getOneUser(id));
    }


    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllParents(){
        return ResponseEntity.ok(userService.getAllList(Role.ROLE_PARENT));
    }



    @GetMapping("/{studentId}/attendance/week")
    public ApiResponse<List<WeekAttendanceDTO>> weekAttendance(
            @AuthenticationPrincipal User parent,
            @PathVariable Long studentId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate weekStart
    ) {
        return userService.getWeekAttendance(parent, studentId, weekStart);
    }

    @GetMapping("/{studentId}/marks/week")
    public ApiResponse<List<WeekMarkDTO>> getWeekMarks(
            @AuthenticationPrincipal User parent,
            @PathVariable Long studentId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart
    ) {
        return userService.getWeekMarks(parent, studentId, weekStart);
    }



    @GetMapping("/{studentId}/stats")
    public ApiResponse<StudentStatsDTO> getStats(
            @AuthenticationPrincipal User parent,
            @PathVariable Long studentId
    ) {
        return userService.getStats(parent, studentId);
    }




}
