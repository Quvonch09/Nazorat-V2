package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.response.ResOption;
import com.example.nazoratv2.dto.response.ResQuestion;
import com.example.nazoratv2.entity.Question;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionMapper {

    public ResQuestion toResponse(Question question) {

        List<ResOption> options = question.getOptions().stream()
                .map(o -> new ResOption(
                        o.getId(),
                        o.getText(),
                        o.getFile()
                ))
                .toList();

        return new ResQuestion(
                question.getId(),
                question.getText(),
                question.getDifficulty(),
                question.getScore(),
                question.getFile(),
                options
        );
    }

}
