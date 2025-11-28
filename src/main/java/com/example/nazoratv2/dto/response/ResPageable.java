package com.example.nazoratv2.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResPageable {

    private int page;
    private int size;
    private int totalPage;
    private long totalElements;
    private Object body;
}
