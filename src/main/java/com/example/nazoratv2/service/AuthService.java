package com.example.nazoratv2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.AuthRegister;
import com.example.nazoratv2.dto.request.ReqStudent;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.entity.enums.Role;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.repository.GroupRepository;
import com.example.nazoratv2.repository.StudentRepository;
import com.example.nazoratv2.repository.UserRepository;
import com.example.nazoratv2.security.CustomUserDetails;
import com.example.nazoratv2.security.JwtService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;

    public ApiResponse<String> login(String phone, String password) {
        Optional<User> optionalUser = userRepository.findByPhone(phone);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ApiResponse.error("Invalid password");
            }

            CustomUserDetails userDetails = CustomUserDetails.fromUser(user);
            String token = jwtService.generateToken(
                    userDetails.getUsername(),
                    userDetails.getRole()
            );

            return ApiResponse.success(token);
        }

        Optional<Student> optionalStudent = studentRepository.findByPhoneNumber(phone);

        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            if (!passwordEncoder.matches(password, student.getPassword())) {
                return ApiResponse.error("Invalid password");
            }

            CustomUserDetails userDetails = CustomUserDetails.fromStudent(student);
            String token = jwtService.generateToken(
                    userDetails.getUsername(),
                    userDetails.getRole()
            );

            return ApiResponse.success(token);
        }

        return ApiResponse.error("User topilmadi");
    }






    public ApiResponse<String> saveUser(AuthRegister authRegister, Role role){

        boolean b = userRepository.existsByPhoneAndRole(authRegister.getPhoneNumber(), role);
        if (b){
            return ApiResponse.error("Teacher already exists");
        }

        User teacher = User.builder()
                .phone(authRegister.getPhoneNumber())
                .fullName(authRegister.getFullName())
                .password(passwordEncoder.encode(authRegister.getPassword()))
                .role(role)
                .build();
        userRepository.save(teacher);
        return ApiResponse.success(null);
    }


    public ApiResponse<String> saveStudent(ReqStudent reqStudent){

        boolean b = studentRepository.existsByPhoneNumber(reqStudent.getPhone());
        boolean b1 = userRepository.existsByPhone(reqStudent.getPhone());

        if (b || b1){
            return ApiResponse.error("User already exists");
        }

        User parent = userRepository.findByPhoneAndRole(reqStudent.getParentPhone(), Role.ROLE_PARENT).orElseThrow(
                () -> new DataNotFoundException("Parent not found")
        );

        Group group = groupRepository.findById(reqStudent.getGroupId()).orElseThrow(
                () -> new DataNotFoundException("Group not found")
        );

        Student student = Student.builder()
                .fullName(reqStudent.getFullName())
                .parent(parent)
                .phoneNumber(reqStudent.getPhone())
                .password(passwordEncoder.encode(reqStudent.getPassword()))
                .group(group)
                .imgUrl(reqStudent.getImgUrl())
                .build();
        studentRepository.save(student);
        return ApiResponse.success(null);
    }
}
