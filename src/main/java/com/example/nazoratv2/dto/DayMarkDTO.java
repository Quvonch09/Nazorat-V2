package com.example.nazoratv2.dto;

import com.example.nazoratv2.entity.enums.MarkCategoryStatus;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DayMarkDTO {
    private String dayName;
    private String subjectName;
    private Integer score;
    private MarkCategoryStatus category;
}