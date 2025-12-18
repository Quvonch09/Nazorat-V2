package com.example.nazoratv2.dto.request;

import com.example.nazoratv2.entity.Category;
import com.example.nazoratv2.entity.Option;
import com.example.nazoratv2.entity.enums.QuestionDifficulty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReqQuestion {

    private String text;
    private QuestionDifficulty difficulty;
    private List<ReqOption> options;
    private Integer score;
    private String file;
    private Long categoryId;

}
