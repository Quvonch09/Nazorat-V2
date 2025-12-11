package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.response.UserResponse;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

public UserResponse toResponseUser(User user) {
    return UserResponse.builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .phone(user.getPhone())
            .imageUrl(user.getImageUrl())
            .role(user.getRole().name())
            .build();
}

//    public UserResponse toResponseStudent(Student student){
//        return new UserResponse(
//                user.getId(),
//                user.getFullName(),
//                user.getPhoneNumber(),
//                user.getGroup().getName(),
//                user.getParent().getFullName(),
//                markRowMapper.level(markRepository.scoreByUserId(user.getId()) != null ?
//                        markRepository.scoreByUserId(user.getId()) : 0).toString(),
//                user.getGroup().getId(),
//                user.getImgUrl(),
//                Role.STUDENT.name()
//        );
//    }
}
