package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.response.ResMark;
import com.example.nazoratv2.entity.Mark;
import org.springframework.stereotype.Component;

@Component
public class MarkMapper {
    public ResMark toDTO(Mark mark) {
        return ResMark.builder()
                .markId(mark.getId())
                .markCategoryStatus(mark.getMarkCategoryStatus() != null ? mark.getMarkCategoryStatus().name() : null)
                .markStatus(mark.getStatus() != null ? mark.getStatus().name() : null)
                .totalScore(mark.getTotalScore())
                .studentId(mark.getStudent() != null ? mark.getStudent().getId() : null)
                .studentName(mark.getStudent() != null ? mark.getStudent().getFullName() : null)
                .build();
    }



    public ResMark toFullDTO(Mark mark) {
        return ResMark.builder()
                .markId(mark.getId())
                .markCategoryStatus(mark.getMarkCategoryStatus() != null ? mark.getMarkCategoryStatus().name() : null)
                .markStatus(mark.getStatus() != null ? mark.getStatus().name() : null)
                .totalScore(mark.getTotalScore())
                .studentId(mark.getStudent() != null ? mark.getStudent().getId() : null)
                .studentName(mark.getStudent() != null ? mark.getStudent().getFullName() : null)
                .activityScore(mark.getActiveScore())
                .homeworkScore(mark.getHomeworkScore())
                .markDate(mark.getDate())
                .build();
    }
}
