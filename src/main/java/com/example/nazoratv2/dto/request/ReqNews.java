package com.example.nazoratv2.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqNews {

    private String title;
    private String description;
    private String imgUrl;
    private String date;
    private Long categoryId;
    private Long markId;
    private Long groupId;

}
