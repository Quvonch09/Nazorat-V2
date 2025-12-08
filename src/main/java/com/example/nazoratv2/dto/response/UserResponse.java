package com.example.nazoratv2.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String groupName;
    private String parentName;
    private String level;
    private Long groupId;
    private String imageUrl;
    private String role;
}
