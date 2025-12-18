package com.example.nazoratv2.dto;

import com.example.nazoratv2.dto.request.ReqOption;
import com.example.nazoratv2.entity.enums.QuestionDifficulty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqQuestionDTO {

    private Long id;

    private String text;

    private QuestionDifficulty difficulty;

    private Integer score;

    private String file;

    private Long categoryId;

    private List<ReqOptionDTO> options;
}
