package com.example.nazoratv2.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResOption {
    private Long id;
    private String text;
    private String file;
}
