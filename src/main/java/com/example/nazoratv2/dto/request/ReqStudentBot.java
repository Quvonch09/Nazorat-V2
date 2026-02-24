package com.example.nazoratv2.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqStudentBot {
    // student
    private String fullName;
    private String phone;
    private Long groupId;
    private String imgUrl;
    private Long studentTelegramId;

    // parent
    private String parentUsername; // "abc" ( @siz )
    private String parentPhone;    // "998..."
    private String parentName;     // agar parent yo'q bo'lsa
}
