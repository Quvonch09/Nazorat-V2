package com.example.nazoratv2.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String groupName;
    private String parentName;
    private String level;
    private Long groupId;
    private String teacherName;
    private String roomName;
    private int lessonCount;
    private String imgUrl;
    private String role;
}
