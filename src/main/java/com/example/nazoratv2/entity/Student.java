package com.example.nazoratv2.entity;

import com.example.nazoratv2.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Student extends BaseEntity {

    private String fullName;

    private String phone;

    private String password;

    private String imgUrl;

    private long telegramId;

    private int coin;

    @ManyToOne
    private User parent;

    @ManyToOne
    private Group group;

}
