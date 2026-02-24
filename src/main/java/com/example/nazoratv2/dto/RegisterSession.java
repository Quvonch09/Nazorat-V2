package com.example.nazoratv2.dto;

import com.example.nazoratv2.entity.enums.Step;
import lombok.Data;

@Data
public class RegisterSession {
    private Step step = Step.STUDENT_PHONE;

    // student
    private String studentName;
    private String studentPhone;
    private Long studentTelegramId;
    private Long groupId;

    // parent
    private String parentUsername; // @abc bo'lsa shu
    private String parentPhone;    // 998...
    private String parentName;     // parent topilmasa yaratish uchun
    private Long parentId; // parent flow uchun
    private Long pickedStudentId; // parent tanlagan bola
}