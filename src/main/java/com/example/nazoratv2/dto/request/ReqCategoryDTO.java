package com.example.nazoratv2.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqCategoryDTO {
    private Long id;

    private String name;

    private String description;

    private int duration;

    private int questionLimit;

    private String imgUrl;

}
