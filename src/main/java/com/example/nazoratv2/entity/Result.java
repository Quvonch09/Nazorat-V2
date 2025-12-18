package com.example.nazoratv2.entity;

import com.example.nazoratv2.entity.base.BaseEntity;
import com.example.nazoratv2.entity.enums.ResultStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Result extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private  Category category;

    @Column(nullable = false)
    private Integer questionCount;

    private Integer totalScore;

    // Tuplangan ball
    private Integer earnedScore;

    // Natija foizda
    private Double percentage;

    // PASSED / FAILED
    @Enumerated(EnumType.STRING)
    private ResultStatus status;

    private Integer attemptNumber;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean retakePermission=false;

    boolean deleted=false;


}
