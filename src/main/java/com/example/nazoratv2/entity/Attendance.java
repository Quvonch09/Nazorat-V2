package com.example.nazoratv2.entity;

import com.example.nazoratv2.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Attendance extends BaseEntity {

    private LocalDate date;

    @ManyToOne
    private Student student;

    private Boolean status; //true -> keldi, false -> kelmadi, null -> sababli

}
