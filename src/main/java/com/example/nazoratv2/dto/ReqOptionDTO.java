package com.example.nazoratv2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqOptionDTO {
    private Long id;
    private String text;
    private boolean correct;
    private String file;
}
