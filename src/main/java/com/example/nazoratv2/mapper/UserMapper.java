package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.response.UserResponse;
import com.example.nazoratv2.entity.User;
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
}
