package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.dashboard.DashboardDTO;
import com.example.nazoratv2.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;


    @GetMapping
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }
}
