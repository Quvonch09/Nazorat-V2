package com.example.nazoratv2.entity;


import com.example.nazoratv2.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class News extends BaseEntity {


    private String name;

    private String description;

    private String imgUrl;

    private LocalDate date;

    private Boolean active;

}
