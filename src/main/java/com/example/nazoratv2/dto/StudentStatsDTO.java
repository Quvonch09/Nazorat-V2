package com.example.nazoratv2.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StudentStatsDTO {
    private Integer attendancePercent;
    private Double averageGrade;
    private Integer subjectsCount;
}