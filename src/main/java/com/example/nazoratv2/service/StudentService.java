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
        return ApiResponse.success(resPageable, "Success");

    }

    public ApiResponse<ResStudent> getById(Long id) {

        Student student = studentRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Student not found"));
        return ApiResponse.success(studentMapper.toStudentDTO(student), "Success");
    }

    public ApiResponse<String> delete(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Student not found"));
        student.setActive(false);
        studentRepository.save(student);
        return ApiResponse.success(null, "Success");
    }

    public ApiResponse<String> update(CustomUserDetails current, StudentDTO req) {

        boolean isAdmin = "ROLE_ADMIN".equals(current.getRole()) || "ROLE_SUPER_ADMIN".equals(current.getRole());

        Student targetStudent;

        if (req.getId() == null) {
            targetStudent = studentRepository.findByPhone(current.getUsername())
                    .orElseThrow(() -> new DataNotFoundException("Student topilmadi"));
        } else {
            if (!isAdmin) {
                return ApiResponse.error("Siz boshqa studentni update qila olmaysiz!");
            }

            targetStudent = studentRepository.findById(req.getId())
                    .orElseThrow(() -> new DataNotFoundException("Student topilmadi: "));
        }

        String oldPhone = targetStudent.getPhone();

        if (req.getPhone() != null) targetStudent.setPhone(req.getPhone());
        if (req.getFullName() != null) targetStudent.setFullName(req.getFullName());
        if (req.getImgUrl() != null) targetStudent.setImgUrl(req.getImgUrl());

        Student saved = studentRepository.save(targetStudent);

        String token = null;

        if (req.getPhone() != null && !req.getPhone().equals(oldPhone)) {
            CustomUserDetails userDetails = CustomUserDetails.fromStudent(saved);
            token = jwtService.generateToken(
                    userDetails.getUsername(),
                    userDetails.getRole()
            );
        }

        return ApiResponse.success(token, "Success");
    }
}
