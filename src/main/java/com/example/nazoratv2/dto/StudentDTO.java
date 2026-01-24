package com.example.nazoratv2.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StudentDTO {
    private Long id;
    private String fullName;
    private String phone;
    private String imgUrl;
}
