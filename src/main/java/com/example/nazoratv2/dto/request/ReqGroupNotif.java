package com.example.nazoratv2.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqGroupNotif {

    private Long groupId;

    private String title;
    private String description;
}
