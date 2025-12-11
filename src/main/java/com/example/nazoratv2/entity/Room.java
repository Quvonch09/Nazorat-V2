package com.example.nazoratv2.entity;

import jakarta.persistence.Entity;
import lombok.*;
import com.example.nazoratv2.entity.base.BaseEntity;
import org.hibernate.annotations.Where;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Where(clause = "active = true")
public class Room extends BaseEntity {
    private String name;
}
