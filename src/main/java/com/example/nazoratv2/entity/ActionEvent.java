package com.example.nazoratv2.entity;

import com.example.nazoratv2.entity.enums.ActionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ActionEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private String description;

    private Long userId;

    private String userRole;

    private String entityName;

    private Long entityId;

    private LocalDateTime createdAt;
}
