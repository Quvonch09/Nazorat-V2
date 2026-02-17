package com.example.nazoratv2.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqNews {

    private String title;
    private String description;
    private String imgUrl;
    private LocalDate date;

}
