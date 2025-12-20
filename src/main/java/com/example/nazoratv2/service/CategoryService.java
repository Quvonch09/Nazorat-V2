package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqCategory;
import com.example.nazoratv2.dto.response.ResCategory;
import com.example.nazoratv2.dto.response.ResOption;
import com.example.nazoratv2.dto.response.ResponseQuestion;
import com.example.nazoratv2.entity.Category;
import com.example.nazoratv2.entity.Question;
import com.example.nazoratv2.entity.Result;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.enums.ResultStatus;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.CategoryMapper;
import com.example.nazoratv2.mapper.QuestionMapper;
import com.example.nazoratv2.repository.CategoryRepository;
import com.example.nazoratv2.repository.QuestionRepository;
import com.example.nazoratv2.repository.ResultRepository;
import com.example.nazoratv2.repository.StudentRepository;
import com.example.nazoratv2.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final ResultRepository resultRepository;
    private final QuestionRepository questionRepository;

    public ApiResponse<String> saveCategory(ReqCategory reqCategory) {

        Category category = Category.builder()
                .name(reqCategory.getName())
                .description(reqCategory.getDescription())
                .duration(reqCategory.getDuration())
                .imgUrl(reqCategory.getImgUrl())
                .questionLimit(reqCategory.getQuestionLimit())
                .build();

        categoryRepository.save(category);
        return ApiResponse.success(null, "Category successfully saved");
    }


    public ApiResponse<String> updateCategory(Long id, ReqCategory reqCategory) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Category not found")
        );

        category.setName(reqCategory.getName());
        category.setDescription(reqCategory.getDescription());
        category.setDuration(reqCategory.getDuration());
        category.setImgUrl(reqCategory.getImgUrl());
        category.setQuestionLimit(reqCategory.getQuestionLimit());

        categoryRepository.save(category);

        return ApiResponse.success(null, "Category successfully updated");
    }


    public ApiResponse<String> deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Category not found")
        );

        category.setActive(false);
        categoryRepository.save(category);
        return ApiResponse.success(null, "Category successfully deleted");
    }


    public ApiResponse<ResCategory> getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Category not found")
        );

        return ApiResponse.success(categoryMapper.toRes(category), "Success");
    }


    public ApiResponse<List<ResCategory>> getAllCategory() {
        List<Category> list = categoryRepository.findAll();

        if (list.isEmpty()) {
            return ApiResponse.error("Category not found");
        }

        List<ResCategory> res = list.stream()
                .map(categoryMapper::toRes)
                .toList();

        return ApiResponse.success(res, "Success");
    }


    public ApiResponse<List<ResponseQuestion>> startTest(CustomUserDetails currentUser, Long categoryId) {

        if (!currentUser.isStudent()) {
            return ApiResponse.error("Only students can start tests");
        }

        Student student = currentUser.getStudent();

        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new DataNotFoundException("Category not found"));

        int limit = category.getQuestionLimit();

        Optional<Result> lastResult =
                resultRepository.findTopByStudentIdAndCategoryIdOrderByAttemptNumberDesc(student.getId(),categoryId);
        if (lastResult.isPresent()) {
            Result r = lastResult.get();

            if (r.getStatus() == ResultStatus.PASSED) {
                return ApiResponse.error("You already passed this quiz");
            }

            if (!r.isRetakePermission()) {
                return ApiResponse.error("Retake not allowed yet");
            }
        }

        int attemptNumber = resultRepository.countByStudentIdAndCategoryId(student.getId(),categoryId) + 1;

        Result result = Result.builder()
                .student(student)
                .category(category)
                .status(ResultStatus.STARTED)
                .attemptNumber(attemptNumber)
                .startTime(LocalDateTime.now())
                .retakePermission(false)
                .questionCount(limit)
                .build();
        Result save = resultRepository.save(result);

        List<Question> allQuestions = questionRepository.findAllByCategoryIdAndDeletedFalse(categoryId);
        if (allQuestions.size() < category.getQuestionLimit()) {
            return ApiResponse.error("Savollar Kategoriya buyicha yetarli emas, savol qo‘shing.");
        }

        if (allQuestions.isEmpty()) {
            throw new DataNotFoundException("No questions found");
        }

        Collections.shuffle(allQuestions);

        List<ResponseQuestion> responseQuestions = allQuestions.stream().map(question -> {
            List<ResOption> options = question.getOptions().stream().map(option -> ResOption.builder()
                    .id(option.getId())
                    .text(option.getText())
                    .file(option.getFile())
                    .build()).collect(Collectors.toList());

            Collections.shuffle(options);

            return ResponseQuestion.builder()
                    .id(question.getId())
                    .resultId(save.getId())
                    .text(question.getText())
                    .difficulty(question.getDifficulty())
                    .categoryId(category.getId())
                    .file(question.getFile())
                    .options(options)
                    .build();
        }).collect(Collectors.toList());
        return ApiResponse.success(responseQuestions, "Category successfully started");
    }

}
