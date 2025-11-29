package com.example.nazoratv2.dto.request;

import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqNotification {

    private String message;
    private String description;
    private Long studentId;
    private Long parentId;
    private boolean isRead;
}
