package com.example.nazoratv2.entity;

import com.example.nazoratv2.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Student extends BaseEntity {

    private String fullName;

    private String phoneNumber;

    private String password;

    private String imgUrl;

    @ManyToOne
    private User parent;

    @ManyToOne
    private Group group;

    private boolean deleted;
}
