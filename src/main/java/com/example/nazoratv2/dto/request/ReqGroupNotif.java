package com.example.nazoratv2.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqGroupNotif {

    private Long groupId;

    private String title;
    private String description;
}
