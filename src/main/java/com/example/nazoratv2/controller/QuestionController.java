package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.ReqQuestionDTO;
import com.example.nazoratv2.dto.request.ReqQuestion;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addQuestion(@RequestBody ReqQuestion req) {
        return ResponseEntity.ok(questionService.createQuestion(req));
    }

//    @GetMapping
//    public ResponseEntity<ApiResponse<ResPageable>> getAllQuestionsByPage(@RequestParam(defaultValue = "0") int page,
//                                                                          @RequestParam(defaultValue = "10") int size) {
//        return ResponseEntity.ok(questionService.getQuestionsByPage(page, size));
//    }

    @PutMapping
    public ResponseEntity<ApiResponse<String>> updateQuestion(@RequestBody ReqQuestionDTO req) {
        return ResponseEntity.ok(questionService.updateQuestion(req));
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<ApiResponse<String>> deleteQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(questionService.deleteQuestion(questionId));
    }




}
