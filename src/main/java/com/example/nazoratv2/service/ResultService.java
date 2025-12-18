package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqResult;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.dto.response.ResResult;
import com.example.nazoratv2.entity.Category;
import com.example.nazoratv2.entity.Result;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.enums.ResultStatus;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.ResultMapper;
import com.example.nazoratv2.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;
    private final StudentRepository studentRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final ResultMapper resultMapper;

    public ApiResponse<ResResult> submitQuiz(ReqResult req) {

        Student student = studentRepository.findById(req.getStudentId())
                .orElseThrow(() -> new DataNotFoundException("Student not found"));

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

        int totalScore = questionRepository.getTotalScoreByCategory(req.getCategoryId());
        int earnedScore = req.getEarnedScore();

        double percentage = totalScore == 0
                ? 0
                : ((double) earnedScore / totalScore) * 100;

        ResultStatus status = percentage >= 70
                ? ResultStatus.PASSED
                : ResultStatus.FAILED;

        int attemptNumber =
                resultRepository.countByStudentIdAndCategoryId(
                        req.getStudentId(),
                        req.getCategoryId()
                ) + 1;

        Result result = Result.builder()
                .student(student)
                .category(category)
                .totalScore(totalScore)
                .earnedScore(earnedScore)
                .percentage(percentage)
                .status(status)
                .attemptNumber(attemptNumber)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .retakePermission(false)
                .build();
        resultRepository.save(result);

        ResResult response = resultMapper.toResponse(result);
        return ApiResponse.success(response, "success");
    }

    public ApiResponse<ResResult> getResultById(Long resultId) {

        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new DataNotFoundException("Result not found"));

        return ApiResponse.success(resultMapper.toResponse(result),"Success");
    }


    public ApiResponse<ResPageable> getResultsByUser(Long studentId, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Result> pageResult = resultRepository.findAllByStudentIdAndDeletedFalse((studentId), pageRequest);

        List<ResResult> list = pageResult.getContent().stream()
                .map(resultMapper::toResponse)
                .toList();
        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(pageResult.getTotalElements())
                .totalPage(pageResult.getTotalPages())
                .body(list)
                .build();

        return ApiResponse.success(resPageable,"success");
    }

    public ApiResponse<String> deleteResult(Long resultId) {

        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new DataNotFoundException("Result not found"));

        result.setDeleted(true);
        resultRepository.save(result);

        return ApiResponse.success(null,"success");
    }

    public ApiResponse<String> grantRetake(Long resultId) {

        Result result = resultRepository.findByIdAndDeletedFalse(resultId)
                .orElseThrow(() -> new DataNotFoundException("Result not found"));

        result.setRetakePermission(true);
        resultRepository.save(result);

        return ApiResponse.success(null,"Retake granted");
    }

    public ApiResponse<ResPageable> getAllResults(int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Result> pageResult = resultRepository.findAllByDeletedFalse(pageRequest);

        List<ResResult> list = pageResult.getContent().stream()
                .map(resultMapper::toResponse)
                .toList();
        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(pageResult.getTotalElements())
                .totalPage(pageResult.getTotalPages())
                .body(list)
                .build();

        return ApiResponse.success(resPageable, "Success");
    }

    public ApiResponse<ResPageable> getResultsByCategory(Long categoryId, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Result> pageResult = resultRepository.findAllByCategoryIdAndDeletedFalse(categoryId, pageRequest);

        List<ResResult> list = pageResult.getContent().stream()
                .map(resultMapper::toResponse)
                .toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(pageResult.getTotalElements())
                .totalPage(pageResult.getTotalPages())
                .body(list)
                .build();
        return ApiResponse.success(resPageable, "Success");
    }


}
