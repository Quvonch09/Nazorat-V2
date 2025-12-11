package com.example.nazoratv2.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRegister {
    private String fullName;
    private String phone;
    private String password;

}
