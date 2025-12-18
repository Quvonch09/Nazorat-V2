package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.ReqOptionDTO;
import com.example.nazoratv2.dto.ReqQuestionDTO;
import com.example.nazoratv2.dto.request.ReqOption;
import com.example.nazoratv2.dto.request.ReqQuestion;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.dto.response.ResQuestion;
import com.example.nazoratv2.entity.Category;
import com.example.nazoratv2.entity.Option;
import com.example.nazoratv2.entity.Question;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.QuestionMapper;
import com.example.nazoratv2.repository.CategoryRepository;
import com.example.nazoratv2.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionMapper questionMapper;

    public ApiResponse<String> createQuestion(ReqQuestion req) {

        Category category = categoryRepository.findById(req.getCategoryId()).
                orElseThrow(() -> new DataNotFoundException("Category not found"));

        if (req.getOptions() == null || req.getOptions().size() < 2) {
            throw new IllegalArgumentException("Kamida 2 ta option bulishi kerak");
        }
        long correctCount = req.getOptions()
                .stream()
                .filter(ReqOption::isCorrect)
                .count();
        if (correctCount != 1) {
            throw new IllegalArgumentException("Faqat bitta tugri javob bulishi kerak");
        }
        Question question = Question.builder()
                .text(req.getText())
                .difficulty(req.getDifficulty())
                .score(req.getScore())
                .file(req.getFile())
                .category(category)
                .build();
        List<Option> options = req.getOptions().stream()
                .map(o -> Option.builder()
                        .text(o.getText())
                        .correct(o.isCorrect())
                        .file(o.getFile())
                        .question(question)
                        .build())
                .toList();
        question.setOptions(options);
        questionRepository.save(question);

        return ApiResponse.success(null,"success");
    }


    public ApiResponse<ResPageable> getQuestionsByPage(int page,int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Question> questionsPage = questionRepository.findAllByDeletedFalse(pageRequest);

        List<ResQuestion> list = questionsPage.stream().map(questionMapper::toResponse).toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(questionsPage.getTotalElements())
                .totalPage(questionsPage.getTotalPages())
                .body(list)
                .build();
        return ApiResponse.success(resPageable, "Success");

    }

    public ApiResponse<String> updateQuestion(ReqQuestionDTO req) {

        Question question = questionRepository.findById(req.getId())
                .orElseThrow(() -> new DataNotFoundException("Question not found"));

        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found"));
            question.setCategory(category);
        }

        if (req.getText() != null) question.setText(req.getText());
        if (req.getDifficulty() != null) question.setDifficulty(req.getDifficulty());
        if (req.getScore() != null) question.setScore(req.getScore());
        if (req.getFile() != null) question.setFile(req.getFile());

        if (req.getOptions() != null && !req.getOptions().isEmpty()) {

            if (req.getOptions().size() < 2) {
                throw new IllegalArgumentException("Kamida 2 ta option bulishi kerak");
            }

            long correctCount = req.getOptions().stream().filter(ReqOptionDTO::isCorrect).count();
            if (correctCount != 1) {
                throw new IllegalArgumentException("Faqat bitta tugri javob bulishi kerak");
            }

            question.getOptions().clear();

            List<Option> options = req.getOptions().stream()
                    .map(o -> Option.builder()
                            .text(o.getText())
                            .correct(o.isCorrect())
                            .file(o.getFile())
                            .question(question)
                            .build())
                    .toList();

            question.setOptions(options);
        }

        questionRepository.save(question);

        return ApiResponse.success(null,"success");

    }

    public ApiResponse<String> deleteQuestion(Long id) {
        Question question = questionRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Question not found"));
        question.setDeleted(true);
        question.getOptions().forEach(option -> option.setDeleted(true));
        questionRepository.save(question);
        return ApiResponse.success(null,"success");
    }

}
