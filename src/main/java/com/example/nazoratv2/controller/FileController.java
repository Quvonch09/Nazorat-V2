package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.nazoratv2.service.CloudService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "Filelar bilan ishlash")
public class FileController {
    private final CloudService cloudService;

    @PostMapping(value = "/upload",consumes = "multipart/form-data")
    @Operation(summary = "Rasm yuklash")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        String url = cloudService.uploadFile(file);
        return ResponseEntity.ok(url);
    }


    @PostMapping(value = "/pdf", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<String>> upload(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(cloudService.uploadFile(file, file.getOriginalFilename()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
