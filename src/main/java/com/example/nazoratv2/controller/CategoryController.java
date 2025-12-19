package com.example.nazoratv2.controller;


import com.example.nazoratv2.configuration.TrackAction;
import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqCategory;
import com.example.nazoratv2.dto.request.ReqStartTest;
import com.example.nazoratv2.dto.response.ResCategory;
import com.example.nazoratv2.dto.response.ResQuestion;
import com.example.nazoratv2.entity.enums.ActionType;
import com.example.nazoratv2.service.CategoryService;
import com.example.nazoratv2.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @TrackAction(
            type = ActionType.CATEGORY_CREATED,
            description = "Category yaratildi"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<String>> saveCategory(@RequestBody ReqCategory reqCategory){
        return ResponseEntity.ok(categoryService.saveCategory(reqCategory));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<String>> updateCategory(@PathVariable Long categoryId, @RequestBody ReqCategory reqCategory){
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, reqCategory));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long categoryId){
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResCategory>>> getCategories(){
        return ResponseEntity.ok(categoryService.getAllCategory());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<ResCategory>> getCategory(@PathVariable Long categoryId){
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    @PostMapping("/start-test")
    public ResponseEntity<ApiResponse<Long>> startTest(@RequestBody ReqStartTest req) {
        return ResponseEntity.ok(categoryService.startTest(req));
    }

    @GetMapping("/question/{categoryId}")
    public ResponseEntity<ApiResponse<List<ResQuestion>>> getRandomQuestionByCategoryId(@PathVariable Long categoryId,
                                                                                        @RequestParam int limit){
        return ResponseEntity.ok(categoryService.getRandomQuestionsByCategory(categoryId,limit));
    }

}
