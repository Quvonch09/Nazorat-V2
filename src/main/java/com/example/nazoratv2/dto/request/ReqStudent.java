package com.example.nazoratv2.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqStudent {
    private String fullName;
    private String phone;
    private String imgUrl;
    private String password;
    private Long groupId;
    private String parentPhone;
}
