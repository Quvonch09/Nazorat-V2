package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.AttendanceDto;
import com.example.nazoratv2.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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


    @DeleteMapping("/attendanceId")
    public ResponseEntity<ApiResponse<String>> deleteAttendance(@RequestParam Long attendanceId) {
        return ResponseEntity.ok(attendanceService.deleteAttendance(attendanceId));
    }



    @GetMapping("/{attendanceId}")
    public ResponseEntity<ApiResponse<AttendanceDto>> getAttendance(@PathVariable Long attendanceId) {
        return ResponseEntity.ok(attendanceService.getAttendance(attendanceId));
    }


    @GetMapping("/stream/{groupId}")
    public SseEmitter stream(@PathVariable Long groupId) {
        SseEmitter emitter = attendanceService.subscribe();

        try {
            // Dastlabki ma'lumotni yuboramiz
            emitter.send(
                    SseEmitter.event()
                            .name("attendance")
                            .data(attendanceService.getAllAttendanceByGroup(groupId))
            );
        } catch (Exception e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }
}
