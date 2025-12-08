package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqNews;
import com.example.nazoratv2.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @PostMapping
    public ApiResponse add(@RequestBody ReqNews req) {
        return newsService.add(req);
    }

    @GetMapping
    public ApiResponse getAll() {
        return newsService.getAll();
    }

    @GetMapping("/{id}")
    public ApiResponse getOne(@PathVariable Long id) {
        return newsService.getOne(id);
    }

    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Long id) {
        return newsService.delete(id);
    }

}
