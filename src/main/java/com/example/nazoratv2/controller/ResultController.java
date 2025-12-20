package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqResult;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.dto.response.ResResult;
import com.example.nazoratv2.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/result")
@RequiredArgsConstructor
public class ResultController {
    private final ResultService resultService;

    @PostMapping("/submit-test")
    public ResponseEntity<ApiResponse<ResResult>> subMitQuestion(@RequestBody ReqResult req){
        return ResponseEntity.ok(resultService.submitQuiz(req));
    }

    @GetMapping("/{resultId}")
    public ResponseEntity<ApiResponse<ResResult>> getResultById(@PathVariable Long resultId){
        return ResponseEntity.ok(resultService.getResultById(resultId));
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<ApiResponse<ResPageable>> getResultsByStudent(@PathVariable Long studentId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(resultService.getResultsByUser(studentId,page,size));

    }

    @DeleteMapping("/{resultId}")
    public ResponseEntity<ApiResponse<String>> deleteResultById(@PathVariable Long resultId){
        return ResponseEntity.ok(resultService.deleteResult(resultId));
    }

    @PutMapping("/{resultId}")
    public ResponseEntity<ApiResponse<String>> grantedRetake(@PathVariable Long resultId){
        return ResponseEntity.ok(resultService.grantRetake(resultId));
    }

    @GetMapping("/get-page")
    public ResponseEntity<ApiResponse<ResPageable>> getAllResults(@RequestParam(defaultValue = "0")int page,
                                                                  @RequestParam(defaultValue = "10")int size){
        return ResponseEntity.ok(resultService.getAllResults(page,size));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<ResPageable>> getResultsByCategory(@PathVariable Long categoryId,
                                                                         @RequestParam(defaultValue = "0")int page,
                                                                         @RequestParam(defaultValue = "10")int size){
        return ResponseEntity.ok(resultService.getResultsByCategory(categoryId,page,size));
    }

}
