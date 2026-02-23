package com.example.nazoratv2.dto;

import com.example.nazoratv2.entity.enums.Step;
import lombok.Data;

@Data
public class RegisterSession {
    private Step step = Step.PARENT_NAME;

    private String parentName;
    private String parentPhone;

    private String studentName;
    private String studentPhone;

    private Long groupId;
    private String studentPassword;
}

