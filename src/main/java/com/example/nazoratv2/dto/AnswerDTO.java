package com.example.nazoratv2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class AnswerDTO {
    private Long questionId;
    private Long optionId;
}
