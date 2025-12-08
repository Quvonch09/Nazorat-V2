package com.example.nazoratv2.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResNotification {
    private Long id;
    private String message;
    private String description;
    private Long studentId;
    private Long parentId;
    private boolean isRead;
}
