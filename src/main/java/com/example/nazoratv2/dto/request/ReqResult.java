package com.example.nazoratv2.dto.request;

import com.example.nazoratv2.dto.AnswerDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ReqResult {
    private Long resultId;
    private List<AnswerDTO> answers;
}
