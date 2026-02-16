package com.example.nazoratv2.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqCoin {
    private String name;
    private String description;
    private String imgUrl;
    private int coin;
}
