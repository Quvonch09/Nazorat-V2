package com.example.nazoratv2.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResNews {
    private Long id;
    private String name;
    private String description;
    private String imgUrl;
    private String date;
}
