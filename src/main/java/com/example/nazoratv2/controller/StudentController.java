package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    public ResponseEntity<ApiResponse<ResPageable>> createStudent(@RequestBody int page,
                                                                  @RequestBody int size) {
        return ResponseEntity.ok(studentService.getStudents(page, size));
    }

}
