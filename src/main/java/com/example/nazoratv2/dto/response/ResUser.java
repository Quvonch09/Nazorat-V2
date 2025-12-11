package com.example.nazoratv2.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResUser {
    private Long id;
    private String fullName;
    private String imageUrl;
    private String phoneNumber;
}
