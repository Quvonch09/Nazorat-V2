package com.example.nazoratv2.dto.response;

import com.example.nazoratv2.entity.enums.ResultStatus;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResResult {
    private Long resultId;

    private Long studentId;
    private Long categoryId;

    private Integer totalScore;
    private Integer earnedScore;
    private Double percentage;

    private ResultStatus status;
    private Integer attemptNumber;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
