package com.example.nazoratv2.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqStudentBot {
    private String parentName;
    private String parentPhone;

    private String fullName;     // student full name
    private String phone;        // student phone
    private String password;     // student password (yoki default)
    private Long groupId;
    private String imgUrl;       // optional

    private Long parentTelegramId;
}
