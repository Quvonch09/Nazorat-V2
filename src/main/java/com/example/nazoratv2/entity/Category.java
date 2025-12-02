package com.example.nazoratv2.entity;

import com.example.nazoratv2.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Category  extends BaseEntity {

    private String name;

    private String description;

    private Integer duration;

    private boolean active;

    private String imgUrl;


}
