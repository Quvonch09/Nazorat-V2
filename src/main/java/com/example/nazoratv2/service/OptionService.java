package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqOption;
import com.example.nazoratv2.dto.response.ResOption;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.entity.Option;
import com.example.nazoratv2.entity.Question;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.QuestionMapper;
import com.example.nazoratv2.repository.OptionRepository;
import com.example.nazoratv2.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionService {
    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    public ApiResponse<String> createOption(Long questionId, ReqOption req) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new DataNotFoundException("Question not found"));

        Option option = Option.builder()
                .text(req.getText())
                .correct(req.isCorrect())
                .file(req.getFile())
                .question(question)
                .build();

        optionRepository.save(option);
        return ApiResponse.success(null,"success");
    }

    public ApiResponse<String> updateOption(Long optionId, ReqOption req) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new DataNotFoundException("Option not found"));

        if (req.getText() != null) option.setText(req.getText());
        option.setCorrect(req.isCorrect());
        if (req.getFile() != null) option.setFile(req.getFile());

        optionRepository.save(option);
        return ApiResponse.success(null,"success");
    }

    public ApiResponse<String> deleteOption(Long optionId) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new DataNotFoundException("Option not found"));

        option.setDeleted(true);
        optionRepository.save(option);
        return ApiResponse.success(null,"success");
    }


    public ApiResponse<ResPageable> getOptionsByQuestionIdByPage(Long questionId, int page, int size) {

        if (!questionRepository.existsById(questionId)) {
            throw new DataNotFoundException("Question not found");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Option> optionsPage = optionRepository.findAllByQuestionIdAndDeletedFalse(questionId, pageable);

        List<ResOption> list = optionsPage.getContent()
                .stream()
                .map(o -> new ResOption(o.getId(), o.getText(), o.getFile()))
                .toList();

        ResPageable resPageable = ResPageable.builder()
                .page(optionsPage.getNumber())
                .size(optionsPage.getSize())
                .totalElements(optionsPage.getTotalElements())
                .totalPage(optionsPage.getTotalPages())
                .body(list)
                .build();

        return ApiResponse.success(resPageable, "Success");
    }

}
