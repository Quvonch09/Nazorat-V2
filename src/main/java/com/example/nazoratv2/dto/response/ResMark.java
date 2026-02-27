package com.example.nazoratv2.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResMark {
    private Long markId;
    private Long studentId;
    private String studentName;
    private String imageUrl;
    private int totalScore;
    private int activityScore;
    private int homeworkScore;
    private String markCategoryStatus;
    private String markStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate markDate;
}
