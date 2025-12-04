package com.example.nazoratv2.entity;

import com.example.nazoratv2.entity.base.BaseEntity;
import com.example.nazoratv2.entity.enums.MarkCategoryStatus;
import com.example.nazoratv2.entity.enums.MarkStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    private Integer totalScore;

    @Enumerated(EnumType.STRING)
    private MarkStatus status;

    @Enumerated(EnumType.STRING)
    private MarkCategoryStatus markCategoryStatus;
}
