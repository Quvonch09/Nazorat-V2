package com.example.nazoratv2.dto;

import com.example.nazoratv2.entity.enums.MarkCategoryStatus;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MyMarksDTO {

    private Long groupId;
    private String groupName;
    private Double marks;
    private MarkCategoryStatus status;

}
