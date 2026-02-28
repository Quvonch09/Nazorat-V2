package com.example.nazoratv2.dto.request;

import com.example.nazoratv2.entity.enums.MarkStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqMarkDTO {
    private Long id;
    private Long studentId;
    private int homeworkScore;
    private int activityScore;
    private int totalScore;
    private MarkStatus markStatus;
}
