package com.example.nazoratv2.dto.response;

import com.example.nazoratv2.entity.enums.QuestionDifficulty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResQuestion {
    private Long id;
    private String text;
    private QuestionDifficulty difficulty;
    private Integer score;
    private String file;
    private List<ResOption> options;
}
