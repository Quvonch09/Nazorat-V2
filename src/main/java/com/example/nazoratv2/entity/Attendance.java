package com.example.nazoratv2.entity;

import com.example.nazoratv2.entity.base.BaseEntity;
import com.example.nazoratv2.entity.enums.AttendaceEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Where(clause = "active = true")
public class Attendance extends BaseEntity {

    private LocalDate date;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Group group;

    @Enumerated(EnumType.STRING)
    private AttendaceEnum status;

    private String description;

}
