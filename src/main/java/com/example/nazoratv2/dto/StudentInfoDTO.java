package com.example.nazoratv2.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StudentInfoDTO {
    private Long studentId;
    private String studentName;
    private Long groupId;
    private String groupName;
    private String avatarUrl;
}