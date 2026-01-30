package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.response.ResStudent;
import com.example.nazoratv2.dto.response.UserResponse;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentMapper {

    public ResStudent toStudentDTO(Student student) {

        return ResStudent.builder()
                .id(student.getId())
                .fulName(student.getFullName())
                .imgUrl(student.getImgUrl())
                .phoneNumber(student.getPhone())
                .groupId(student.getGroup().getId())
                .groupName(student.getGroup().getName())
                .build();
    }

    public UserResponse toResponseUser(Student user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .imageUrl(user.getImgUrl())
                .role("ROLE_STUDENT")
                .build();
    }
}
