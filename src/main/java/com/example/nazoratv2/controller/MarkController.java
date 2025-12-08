package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqMark;
import com.example.nazoratv2.dto.response.ResMark;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.security.CustomUserDetails;
import com.example.nazoratv2.service.MarkService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mark")
@RequiredArgsConstructor
public class MarkController {
    private final MarkService markService;

    @PostMapping
    @Operation(description = "MarkStatus = KUNLIK_BAHO, IMTIHON_BAHO\n " +
            "Agar imtihon baho bulsa faqat totalScore tuldiriladi, qolgani keremas")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse<String>> saveMark(@RequestBody ReqMark reqMark){
        return ResponseEntity.ok(markService.saveMark(reqMark));
    }



    @PutMapping("/{markId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @Operation(description = "MarkStatus = KUNLIK_BAHO, IMTIHON_BAHO\n " +
            "Agar imtihon baho bulsa faqat totalScore tuldiriladi, qolgani keremas")
    public ResponseEntity<ApiResponse<String>> updateMark(@PathVariable Long markId, @RequestBody ReqMark reqMark){
        return ResponseEntity.ok(markService.updateMark(markId, reqMark));
    }


    @DeleteMapping("/{markId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @Operation(description = "MarkStatus = KUNLIK_BAHO, IMTIHON_BAHO\n " +
            "Agar imtihon baho bulsa faqat totalScore tuldiriladi, qolgani keremas")
    public ResponseEntity<ApiResponse<String>> deleteMark(@PathVariable Long markId){
        return ResponseEntity.ok(markService.deleteMark(markId));
    }


    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Faqat admin uchun barcha marklarni kurish")
    public ResponseEntity<ApiResponse<ResPageable>> getForAdmin(@RequestParam(required = false) String keyword,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(markService.getAllMarkForAdmin(keyword, page, size));
    }


    @GetMapping("/myMarks")
    @Operation(summary = "Teacher, Student, Parent uziga tegishli baholarni kurish")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_PARENT', 'STUDENT')")
    public ResponseEntity<ApiResponse<ResPageable>> getMyMarks(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(markService.getMyMarks(customUserDetails, page, size));
    }


    @GetMapping("/{markId}")
    public ResponseEntity<ApiResponse<ResMark>> getONeMark(@PathVariable Long markId){
        return ResponseEntity.ok(markService.getOneMark(markId));
    }
}
