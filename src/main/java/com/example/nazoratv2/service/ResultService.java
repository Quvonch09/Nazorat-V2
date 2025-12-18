package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.AnswerDTO;
import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqResult;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.dto.response.ResResult;
import com.example.nazoratv2.entity.*;
import com.example.nazoratv2.entity.enums.ResultStatus;
import com.example.nazoratv2.exception.BadRequestException;
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
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final ResultMapper resultMapper;

    public ApiResponse<ResResult> submitQuiz(ReqResult req) {

        Result result = resultRepository.findById(req.getResultId())
                .orElseThrow(() -> new DataNotFoundException("Result not found"));

        if (result.getStatus() != ResultStatus.STARTED) {
            throw new BadRequestException("Test already finished");
        }

        int earnedScore = 0;

        for (AnswerDTO answer : req.getAnswers()) {

            Question question = questionRepository.findByIdAndDeletedFalse(
                            answer.getQuestionId())
                    .orElseThrow(() -> new DataNotFoundException("Question not found"));

            if (!question.getCategory().getId()
                    .equals(result.getCategory().getId())) {
                throw new BadRequestException("Question does not belong to this test");
            }

            Option option = optionRepository.findByIdAndDeletedFalse(
                            answer.getOptionId())
                    .orElseThrow(() ->
                            new DataNotFoundException("Option not found"));

            if (!option.getQuestion().getId()
                    .equals(question.getId())) {
                throw new BadRequestException("Option does not belong to this question");
            }

            if (option.isCorrect()) {
                earnedScore += question.getScore();
            }
        }

        int totalScore =
                questionRepository.getTotalScoreByCategory(
                        result.getCategory().getId()
                );

        double percentage = totalScore == 0
                ? 0
                : ((double) earnedScore / totalScore) * 100;

        ResultStatus status =
                percentage >= 70
                        ? ResultStatus.PASSED
                        : ResultStatus.FAILED;

        result.setEarnedScore(earnedScore);
        result.setTotalScore(totalScore);
        result.setPercentage(percentage);
        result.setStatus(status);
        result.setEndTime(LocalDateTime.now());
        result.setRetakePermission(false);

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
