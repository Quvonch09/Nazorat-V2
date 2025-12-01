package com.example.nazoratv2.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResCategory {

    private Long id;

    private String name;

    private String description;

    private String imgUrl;

}
