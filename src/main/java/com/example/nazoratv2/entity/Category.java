package com.example.nazoratv2.entity;

import com.example.nazoratv2.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.Where;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Where(clause = "active = true")
public class Category  extends BaseEntity {

    private String name;

    private String description;

    private Integer duration;

    private int questionLimit;

    private String imgUrl;


}
