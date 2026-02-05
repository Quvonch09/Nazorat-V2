package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.UserDTO;
import com.example.nazoratv2.dto.request.ReqGroupDTO;
import com.example.nazoratv2.dto.response.ResGroup;
import com.example.nazoratv2.dto.response.ResStudent;
import com.example.nazoratv2.dto.response.ResTeacher;
import com.example.nazoratv2.dto.response.UserResponse;
import com.example.nazoratv2.entity.User;
import org.springframework.stereotype.Component;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Component
public class UserMapper {

    public UserResponse toResponseUser(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .phone(user.getPhone())
            .imgUrl(user.getImageUrl())
            .role(user.getRole().name())
            .build();
    }

//    public UserDTO toUserDTO(User user) {
//    return UserDTO.builder()
//            .id(user.getId())
//            .fullName(user.getFullName())
//            .phone(user.getPhone())
//            .imageUrl(user.getImageUrl())
//            .build();
//    }

    public ResTeacher resTeacher(User teacher, List<ResStudent> studentList, List<ReqGroupDTO> groupList) {
        return ResTeacher.builder()
                .id(teacher.getId())
                .fullName(teacher.getFullName())
                .phone(teacher.getPhone())
                .imageUrl(teacher.getImageUrl())
                .studentList(studentList)
                .groupList(groupList)
                .build();
    }
}
