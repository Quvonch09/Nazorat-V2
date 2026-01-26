package com.example.nazoratv2.service;

import com.example.nazoratv2.configuration.TrackAction;
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
import com.example.nazoratv2.entity.enums.ActionType;
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


    @TrackAction(
            type = ActionType.QUESTION_CREATED,
            description = "Savol yaratildi"
    )
    public ApiResponse<String> createQuestion(ReqQuestion req) {

        Category category = categoryRepository.findById(req.getCategoryId()).
                orElseThrow(() -> new DataNotFoundException("Category not found"));

        int limit = category.getQuestionLimit();

        if (limit <= 0) {
            return ApiResponse.error("Bu kategoriyaga savol qo‘shib bo‘lmaydi ");
        }

        long currentCount = questionRepository.countByCategoryId(category.getId());

        if (currentCount >= limit) {
            return ApiResponse.error("Bu kategoriyada savollar soni limitga yetgan: "
                    + currentCount + "/" + limit);
        }

        if (req.getOptions() == null || req.getOptions().size() < 2) {
            return ApiResponse.error("Kamida 2 ta option bulishi kerak");
        }

        long correctCount = req.getOptions()
                .stream()
                .filter(ReqOption::isCorrect)
                .count();
        if (correctCount != 1) {
            return ApiResponse.error("Faqat bitta tugri javob bulishi kerak");
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

    public ApiResponse<ResPageable> getAllQuestions(Long categoryId, int page, int size) {
        if (categoryId == null) {
            return ApiResponse.error("category Id null bo‘lishi mumkin emas");
        }

        boolean exists = categoryRepository.existsById(categoryId);
        if (!exists) {
            return ApiResponse.error("Category topilmadi !!!");
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Question> questions = questionRepository.findAllByCategoryId(categoryId, pageable);

        if (questions.isEmpty()) {
            return ApiResponse.error("Savollar topilmadi");
        }

        List<ResQuestion> list = questions.stream()
                .map(questionMapper::toQuestionResponse)
                .toList();

        ResPageable res = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(questions.getTotalElements())
                .totalPage(questions.getTotalPages())
                .body(list)
                .build();

        return ApiResponse.success(res, "success");

    }

}
