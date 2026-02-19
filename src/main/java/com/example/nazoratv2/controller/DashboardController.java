package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.MyMarksDTO;
import com.example.nazoratv2.dto.TopStudentDto;
import com.example.nazoratv2.dto.ScheduleResponseDTO;
import com.example.nazoratv2.dto.dashboard.DashboardDTO;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.enums.GroupEnum;
import com.example.nazoratv2.service.DashboardService;
import com.example.nazoratv2.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    @GetMapping("/top-students")
    public ApiResponse<List<TopStudentDto>> topStudents() {
        return studentService.getTop5Students();
    }

    @GetMapping("/schedule")
    public ResponseEntity<ApiResponse<List<ScheduleResponseDTO>>> getSchedule(
            @RequestParam(required = false)GroupEnum groupEnum) {
        return ResponseEntity.ok(dashboardService.getSchedule(groupEnum));
    }


}
