package com.example.nazoratv2.entity;

import com.example.nazoratv2.entity.base.BaseEntity;
import com.example.nazoratv2.entity.enums.QuestionDifficulty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Question extends BaseEntity {

    @Column(nullable = false, length = 1000)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionDifficulty difficulty;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Option> options = new ArrayList<>();

    @Column(nullable = false)
    private Integer score;

    private String file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private boolean deleted=false;


}
