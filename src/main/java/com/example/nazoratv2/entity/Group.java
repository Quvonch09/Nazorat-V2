package com.example.nazoratv2.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import com.example.nazoratv2.entity.base.BaseEntity;
import com.example.nazoratv2.entity.enums.WeekDays;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "groups")
public class Group extends BaseEntity {

    private String name;

    @Column(name = "start_time")
    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @Column(name = "end_time")
    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<WeekDays> weekDays;

    @ManyToOne
    private Category category;

    @ManyToOne
    private User teacher;

    @ManyToOne
    private Room room;

}
