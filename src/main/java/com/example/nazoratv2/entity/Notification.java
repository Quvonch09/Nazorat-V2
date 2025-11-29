package com.example.nazoratv2.entity;

import com.example.nazoratv2.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Notification extends BaseEntity {

    private String message;
    private String description;
    @ManyToMany
    private List<Student> students;
    @ManyToMany
    private List<User> parent;
    private boolean isRead;

}
