package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.AttendanceDto;
import com.example.nazoratv2.entity.Attendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public AttendanceDto toDto(Attendance attendance) {
        return AttendanceDto.builder()
                .id(attendance.getId())
                .date(attendance.getDate())
                .description(attendance.getDescription())
                .fullName(attendance.getStudent().getFullName())
                .status(attendance.getStatus())
                .studentId(attendance.getStudent().getId())
                .build();
    }
}
