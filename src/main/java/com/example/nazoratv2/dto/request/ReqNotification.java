package com.example.nazoratv2.dto.request;

import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqNotification {

    private String message;
    private String description;
    private Long studentId;
    private Long parentId;
}
