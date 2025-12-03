package com.example.nazoratv2.entity;

import com.example.nazoratv2.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Mark extends BaseEntity {

    private LocalDate date;
    @ManyToOne
    private Student student;

    private Integer homeworkScore;

    private Integer activeScore;

    private String status;
}
