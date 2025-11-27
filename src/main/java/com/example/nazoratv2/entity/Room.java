package com.example.nazoratv2.entity;

import jakarta.persistence.Entity;
import lombok.*;
import com.example.nazoratv2.entity.base.BaseEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Room extends BaseEntity {
    private String name;
}
