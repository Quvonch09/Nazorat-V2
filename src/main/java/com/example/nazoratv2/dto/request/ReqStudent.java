package com.example.nazoratv2.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqStudent {
    private String fullName;

    @Pattern(regexp = "^998(9[012345789]|6[0123456789]|7[0123456789]|8[0123456789]|3[0123456789]|5[0123456789])[0-9]{7}$",
    message = "Telefon raqam xato kiritilgan")
    private String phone;
    private String imgUrl;
    private String password;
    private Long groupId;

    @Pattern(regexp = "^998(9[012345789]|6[0123456789]|7[0123456789]|8[0123456789]|3[0123456789]|5[0123456789])[0-9]{7}$",
    message = "Telefon raqam xato kiritilgan")
    private String parentPhone;
    private String parentName;
}
