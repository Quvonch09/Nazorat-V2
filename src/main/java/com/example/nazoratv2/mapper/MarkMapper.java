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
                .markDate(mark.getDate())
                .build();
    }


    public ResMark toMarkDTO(Mark mark) {
        return ResMark.builder()
                .markId(mark.getId())
                .studentId(mark.getStudent() != null ? mark.getStudent().getId() : null)
                .studentName(mark.getStudent() != null ? mark.getStudent().getFullName() : null)
                .imageUrl(mark.getStudent() != null ? mark.getStudent().getImgUrl() : null)
                .totalScore(mark.getTotalScore())
                .activityScore(mark.getActiveScore()!= null ? mark.getActiveScore() : 0)
                .homeworkScore(mark.getHomeworkScore() != null ? mark.getHomeworkScore() : 0)
                .markStatus(mark.getStatus() != null ? mark.getStatus().name() : null)
                .markCategoryStatus(mark.getMarkCategoryStatus() != null ? mark.getMarkCategoryStatus().name() : null)
                .markDate(mark.getDate() != null ? mark.getDate() : null)
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
