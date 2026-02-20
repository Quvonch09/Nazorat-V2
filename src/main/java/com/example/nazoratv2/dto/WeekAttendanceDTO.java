package com.example.nazoratv2.dto;


import com.example.nazoratv2.entity.enums.WeekDays;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WeekAttendanceDTO {
    private WeekDays day;     // MONDAY...
    private boolean present; // true/false
}