package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.UserDTO;
import com.example.nazoratv2.dto.request.ReqStudent;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.dto.response.ResStudent;
import com.example.nazoratv2.dto.response.UserResponse;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.UserMapper;
import com.example.nazoratv2.repository.UserRepository;
import com.example.nazoratv2.security.CustomUserDetails;
import com.example.nazoratv2.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final JwtService jwtService;


    public ApiResponse<UserResponse> getProfile(CustomUserDetails currentUser) {
        User user = currentUser.getUser();
        return ApiResponse.success(mapper.toResponseUser(user),"success");
    }

    public ApiResponse<String> update(CustomUserDetails current , UserDTO req) {

        User user = current.getUser();
        String oldPhone = user.getPhone();
        String newPhone = req.getPhone();

        if (req.getFullName() != null)
            user.setFullName(req.getFullName());

        if (req.getImageUrl() != null)
            user.setImageUrl(req.getImageUrl());

        String newToken = null;

        if (newPhone != null && !newPhone.equals(oldPhone)) {
            user.setPhone(newPhone);
            newToken = jwtService.generateToken(newPhone, user.getRole().name());
        }
        userRepository.save(user);

        return ApiResponse.success(newToken,"success");

    }

    public ApiResponse<String> deleteById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("user not found"));
        user.setEnabled(false);
        userRepository.save(user);
        return ApiResponse.success(null,"success");
    }

    public ApiResponse<UserResponse> getOneUser(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new DataNotFoundException("User topilmadi"));
        return ApiResponse.success(mapper.toResponseUser(user), "Success");
    }


    public ApiResponse<ResPageable> getAllUsersPage(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);

        List<UserResponse> list = users.getContent().stream().map(mapper::toResponseUser).toList();

        ResPageable resPageable = ResPageable.builder()
                    .page(page)
                    .size(size)
                    .totalElements(users.getTotalElements())
                    .totalPage(users.getTotalPages())
                    .body(list)
                    .build();
            return ApiResponse.success(resPageable, null);
    }
}
