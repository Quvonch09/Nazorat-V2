package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.response.ResStudent;
import com.example.nazoratv2.entity.Student;
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
}
