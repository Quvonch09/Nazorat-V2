package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.response.ResResult;
import com.example.nazoratv2.entity.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class ResultMapper {

    public ResResult toResponse(Result result) {
        return ResResult.builder()
                .resultId(result.getId())
                .studentId(result.getStudent().getId())
                .studentName(result.getStudent().getFullName())
                .categoryId(result.getCategory().getId())
                .categoryName(result.getCategory().getName())
                .totalScore(result.getTotalScore())
                .earnedScore(result.getEarnedScore())
                .percentage(result.getPercentage())
                .status(result.getStatus())
                .attemptNumber(result.getAttemptNumber())
                .startTime(result.getStartTime())
                .endTime(result.getEndTime())
                .build();
    }
}
