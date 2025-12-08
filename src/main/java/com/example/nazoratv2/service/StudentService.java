package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.StudentDTO;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.dto.response.ResStudent;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.StudentMapper;
import com.example.nazoratv2.repository.StudentRepository;
import com.example.nazoratv2.repository.UserRepository;
import com.example.nazoratv2.security.CustomUserDetails;
import com.example.nazoratv2.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public ApiResponse<ResPageable> getStudents(int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Student> students = studentRepository.findAll(pageRequest);

        List<ResStudent> list = students.stream().map(studentMapper::toStudentDTO).toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(students.getTotalElements())
                .totalPage(students.getTotalPages())
                .body(list)
                .build();
        return ApiResponse.success(resPageable);

    }

    public ApiResponse<ResStudent> getById(Long id) {

        Student student = studentRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Student not found"));
        return ApiResponse.success(studentMapper.toStudentDTO(student));
    }

    public ApiResponse<String> delete(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Student not found"));
        studentRepository.delete(student);
        return ApiResponse.success(null);
    }

    public ApiResponse<String> update(CustomUserDetails user, StudentDTO req) {

        if ("STUDENT".equals(user.getRole())) {
            Student student = studentRepository.findByPhoneNumber(user.getUsername())
                    .orElseThrow(() -> new RuntimeException("Student topilmadi"));

            student.setPhoneNumber(req.getPhone());
            student.setFullName(req.getFullName());
            student.setImgUrl(req.getImgUrl());
            Student save = studentRepository.save(student);
            if (req.getPhone().equals(user.getPhone())) {
                CustomUserDetails userDetails = CustomUserDetails.fromStudent(save);
                String token = jwtService.generateToken(
                        userDetails.getUsername(),
                        userDetails.getRole()
                );
                return ApiResponse.success(token);
            } else {
                return ApiResponse.success(null);
            }
        } else {
            User user1 = userRepository.findByPhone(user.getUsername())
                    .orElseThrow(() -> new RuntimeException("User topilmadi"));
            user1.setPhone(req.getPhone());
            user1.setFullName(req.getFullName());
            user1.setImageUrl(req.getImgUrl());
            User save = userRepository.save(user1);
            if (req.getPhone().equals(user.getPhone())) {
                String token = jwtService.generateToken(save.getPhone(), save.getRole().name());
                return ApiResponse.success(token);
            } else {
                return ApiResponse.success(null);
            }
        }
    }
}
