package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.StudentDTO;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.dto.response.ResStudent;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.security.CustomUserDetails;
import com.example.nazoratv2.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/get-page-students")
    public ResponseEntity<ApiResponse<ResPageable>> getStudentsByPage(@RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(studentService.getStudents(page, size));
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<ApiResponse<ResStudent>> getById(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getById(studentId));
    }
    @DeleteMapping("/{studentId}")
    public ResponseEntity<ApiResponse<String>> deleteById(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.delete(studentId));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<String>> update(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @RequestBody StudentDTO studentDTO){
        return ResponseEntity.ok(studentService.update(customUserDetails, studentDTO));

    }

}
