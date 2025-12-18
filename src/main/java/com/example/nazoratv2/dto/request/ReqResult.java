package com.example.nazoratv2.dto.request;

import com.example.nazoratv2.dto.AnswerDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ReqResult {
    Long studentId;
    Long categoryId;
    int earnedScore;
    private LocalDateTime startTime;
    private List<AnswerDTO> answers;
}
