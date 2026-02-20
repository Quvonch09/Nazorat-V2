package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.MyMarksDTO;
import com.example.nazoratv2.dto.TopStudentDto;
import com.example.nazoratv2.dto.ScheduleResponseDTO;
import com.example.nazoratv2.dto.dashboard.DashboardDTO;
import com.example.nazoratv2.dto.dashboard.TeacherDashboard;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.entity.enums.GroupEnum;
import com.example.nazoratv2.security.CustomUserDetails;
import com.example.nazoratv2.service.DashboardService;
import com.example.nazoratv2.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final StudentService studentService;


    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    @GetMapping("/teacher")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse<TeacherDashboard>> getDashboardTeacher(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(dashboardService.getTeacherDashboard(customUserDetails));
    }

    @GetMapping("/top-students")
    public ResponseEntity<ApiResponse<List<TopStudentDto>>> topStudents(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(studentService.getTop5Students(user));
    }

    @GetMapping("/schedule")
    public ResponseEntity<ApiResponse<List<ScheduleResponseDTO>>> getSchedule(
            @RequestParam(required = false)GroupEnum groupEnum,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(dashboardService.getSchedule(customUserDetails,groupEnum));
    }


}
