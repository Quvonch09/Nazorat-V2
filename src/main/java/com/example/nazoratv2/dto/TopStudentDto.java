package com.example.nazoratv2.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TopStudentDto {
    private Long studentId;
    private String studentName;
    private Integer totalScore;
    private String imageUrl;
}