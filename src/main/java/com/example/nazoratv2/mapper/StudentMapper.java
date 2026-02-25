package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.response.ResStudent;
import com.example.nazoratv2.dto.response.StudentResponse;
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
                .groupId(student.getGroup() != null ? student.getGroup().getId() : null)
                .groupName(student.getGroup() != null ? student.getGroup().getName() : null)
                .parentId(student.getParent() != null ? student.getParent().getId() : null)
                .parentName(student.getParent() != null ? student.getParent().getFullName() : null)
                .parentPhone(student.getParent() != null ? student.getParent().getPhone() : null)
                .build();
    }

    public StudentResponse toResponseUser(Student user) {
        return StudentResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .imgUrl(user.getImgUrl())
                .role("ROLE_STUDENT")
                .groupId(user.getGroup() != null ? user.getGroup().getId() : null)
                .groupName(user.getGroup() != null ? user.getGroup().getName() : null)
                .parentName(user.getParent() != null ? user.getParent().getFullName() : null)
                .roomName(user.getGroup().getRoom() != null ? user.getGroup().getRoom().getName() : null)
                .teacherName(user.getGroup().getTeacher() != null ? user.getGroup().getTeacher().getFullName() : null)
                .lessonCount(user.getGroup() != null ? user.getGroup().getWeekDays().size() : 0)
                .build();
    }
}
