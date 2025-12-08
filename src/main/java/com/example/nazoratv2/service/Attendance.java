package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Attendance {
    private final AttendanceRepository attendanceRepository;


    public ApiResponse<String> saveAttendance(){
        return null;
    }
}
