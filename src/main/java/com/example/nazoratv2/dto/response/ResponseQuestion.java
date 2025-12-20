package com.example.nazoratv2.dto.response;

import com.example.nazoratv2.entity.enums.QuestionDifficulty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseQuestion {
    private Long id;
    private Long resultId;
    private String text;
    private Integer score;
    private QuestionDifficulty difficulty;
    private Long categoryId;
    private String file;
    private List<ResOption> options;

}
