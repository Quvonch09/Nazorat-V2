package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.AttendanceDto;
import com.example.nazoratv2.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping
    @Operation(summary = "Guruh buyicha davomat qilish",
            description = "AttendanceEnum -> KELDI, KELMADI, SABABLI \n " +
                    "Keldi bulsa status description null buladi, aks holda desciption yoziladi")
    public ResponseEntity<ApiResponse<String>> saveAttendance(@RequestParam Long groupId,
                                                              @RequestBody List<AttendanceDto> attendanceDtoList) {
        return ResponseEntity.ok(attendanceService.saveAttendance(groupId, attendanceDtoList));
    }
}
