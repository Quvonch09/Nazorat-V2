package com.example.nazoratv2.entity;

import com.example.nazoratv2.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Notification extends BaseEntity {

    private String message;
    private String description;
    @ManyToOne
    private Student student;
    @ManyToOne
    private User parent;
    private boolean isRead;

}
