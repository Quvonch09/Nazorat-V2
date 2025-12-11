package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.UserDTO;
import com.example.nazoratv2.dto.request.ReqStudent;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.dto.response.ResStudent;
import com.example.nazoratv2.dto.response.UserResponse;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.entity.enums.Role;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.StudentMapper;
import com.example.nazoratv2.mapper.UserMapper;
import com.example.nazoratv2.repository.StudentRepository;
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
    private final StudentRepository studentRepository;
    private final UserMapper mapper;
    private final JwtService jwtService;

//    public ApiResponse<UserResponse> getProfile(CustomUserDetails currentUser) {
//        if ("STUDENT".equals(currentUser.getRole())) {
//            Student student = studentRepository.findByPhoneNumber(currentUser.getUsername())
//                    .orElseThrow(() -> new RuntimeException("Student topilmadi"));
//            return ApiResponse.success(mapper.toResponseStudent(student), "Success");
//        } else {
//            User user = userRepository.findByPhone(currentUser.getUsername())
//                    .orElseThrow(() -> new RuntimeException("User topilmadi"));
//            return ApiResponse.success(mapper.toResponse(user), "Success");
//        }
//    }



    public ApiResponse<UserResponse> getProfile(CustomUserDetails currentUser) {
        User user = userRepository.findByPhone(currentUser.getUsername()).
                orElseThrow(() -> new DataNotFoundException("User not found"));
        return ApiResponse.success(mapper.toResponseUser(user),"success");
    }



    public ApiResponse<UserResponse> getOneUser(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new DataNotFoundException("User topilmadi"));
        return ApiResponse.success(mapper.toResponseUser(user), "Success");
    }

//    public ApiResponse<ResPageable> searchUsers(CustomUserDetails currentUser, String name,
//                                                String phone, Role role, int page, int size){
//        if (role.equals(Role.STUDENT)){
//            Page<Student> students;
//
//            if (currentUser.getRole().equals(Role.TEACHER.name())){
//                students = studentRepository.searchStudent(name, phone, currentUser.getFullName(), PageRequest.of(page, size));
//            } else {
//                students = studentRepository.searchStudent(name, phone,null, PageRequest.of(page, size));
//            }
//            if (students.getTotalElements() == 0) {
//                return ApiResponse.error("Studentlar topilmadi");
//            }
//
//            List<UserResponse> list = students.getContent().stream().map(mapper::toResponseStudent).toList();
//
//            ResPageable resPageable = ResPageable.builder()
//                    .page(page)
//                    .size(size)
//                    .totalElements(students.getTotalElements())
//                    .totalPage(students.getTotalPages())
//                    .body(list)
//                    .build();
//            return ApiResponse.success(resPageable, null);
//
//        } else {
//            Page<User> users = userRepository.searchUser(
//                    name, phone, role.name(), PageRequest.of(page, size));
//            if (users.getTotalElements() == 0){
//                return ApiResponse.error("Userlar topilmadi");
//            }
//
//            List<UserResponse> list = users.getContent().stream().map(mapper::toResponse).toList();
//
//            ResPageable resPageable = ResPageable.builder()
//                    .page(page)
//                    .size(size)
//                    .totalElements(users.getTotalElements())
//                    .totalPage(users.getTotalPages())
//                    .body(list)
//                    .build();
//            return ApiResponse.success(resPageable, null);
//        }
//    }


//    public ApiResponse<String> updateProfile(CustomUserDetails user, UserDTO userDTO){
//        if ("STUDENT".equals(user.getRole())) {
//            Student student = studentRepository.findByPhoneNumber(user.getUsername())
//                    .orElseThrow(() -> new RuntimeException("Student topilmadi"));
//            student.setPhoneNumber(userDTO.getPhone());
//            student.setFullName(userDTO.getFullName());
//            student.setImgUrl(userDTO.getImageUrl());
//            Student save = studentRepository.save(student);
//            if (userDTO.getPhone().equals(user.getPhone())) {
//                CustomUserDetails userDetails = CustomUserDetails.fromStudent(save);
//                String token = jwtService.generateToken(
//                        userDetails.getUsername(),
//                        userDetails.getRole()
//                );
//                return ApiResponse.success(token, "STUDENT");
//            } else {
//                return ApiResponse.success(null, "Success");
//            }
//        } else {
//            User user1 = userRepository.findByPhone(user.getUsername())
//                    .orElseThrow(() -> new RuntimeException("User topilmadi"));
//            user1.setPhone(userDTO.getPhone());
//            user1.setFullName(userDTO.getFullName());
//            user1.setImageUrl(userDTO.getImageUrl());
//            User save = userRepository.save(user1);
//            if (userDTO.getPhone().equals(user.getPhone())) {
//                String token = jwtService.generateToken(save.getPhone(), save.getRole().name());
//                return ApiResponse.success(token, save.getRole().name());
//            } else {
//                return ApiResponse.success(null, "Success");
//            }
//        }
//    }

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
