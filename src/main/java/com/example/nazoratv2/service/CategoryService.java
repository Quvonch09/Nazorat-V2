package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqCategory;
import com.example.nazoratv2.dto.request.ReqGroup;
import com.example.nazoratv2.dto.request.ReqStartTest;
import com.example.nazoratv2.dto.response.ResCategory;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.dto.response.ResQuestion;
import com.example.nazoratv2.entity.Category;
import com.example.nazoratv2.entity.Question;
import com.example.nazoratv2.entity.Result;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.enums.ResultStatus;
import com.example.nazoratv2.exception.BadRequestException;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.CategoryMapper;
import com.example.nazoratv2.mapper.QuestionMapper;
import com.example.nazoratv2.repository.CategoryRepository;
import com.example.nazoratv2.repository.QuestionRepository;
import com.example.nazoratv2.repository.ResultRepository;
import com.example.nazoratv2.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final StudentRepository studentRepository;
    private final ResultRepository resultRepository;
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

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


    public ApiResponse<Long> startTest(ReqStartTest req) {

        Student student = studentRepository.findById(req.getStudentId())
                .orElseThrow(() -> new DataNotFoundException("Student not found"));

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

        Optional<Result> lastResult =
                resultRepository.findTopByStudentIdAndCategoryIdOrderByAttemptNumberDesc(
                        req.getStudentId(),
                        req.getCategoryId()
                );

        if (lastResult.isPresent()) {
            Result r = lastResult.get();

            if (r.getStatus() == ResultStatus.PASSED) {
                return ApiResponse.error("You already passed this quiz");
            }

            if (!r.isRetakePermission()) {
                return ApiResponse.error("Retake not allowed yet");
            }
        }

        int attemptNumber =
                resultRepository.countByStudentIdAndCategoryId(
                        req.getStudentId(),
                        req.getCategoryId()
                ) + 1;

        Result result = Result.builder()
                .student(student)
                .category(category)
                .status(ResultStatus.STARTED)
                .attemptNumber(attemptNumber)
                .startTime(LocalDateTime.now())
                .retakePermission(false)
                .questionCount(0)
                .build();

        resultRepository.save(result);

        return ApiResponse.success(result.getId(), "Test started");
    }


    public ApiResponse<List<ResQuestion>> getRandomQuestionsByCategory(Long categoryId, int limit) {

        List<Question> questions = questionRepository.findAllByCategoryIdAndDeletedFalse(categoryId);

        if (questions.isEmpty()) {
            throw new DataNotFoundException("No questions found");
        }

        Collections.shuffle(questions);

        List<ResQuestion> list = questions.stream()
                .limit(limit)
                .map(questionMapper::toQuestionResponse)
                .toList();

        return ApiResponse.success(list, "Success");
    }


}
