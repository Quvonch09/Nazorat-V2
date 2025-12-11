package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqNews;
import com.example.nazoratv2.dto.response.ResNews;
import com.example.nazoratv2.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> add(@RequestBody ReqNews req) {
        return ResponseEntity.ok(newsService.add(req));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResNews>>> getAll() {
        return ResponseEntity.ok(newsService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResNews>> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getOne(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.delete(id));
    }

}
