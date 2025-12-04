package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoinService {

    private final StudentRepository studentRepository;

    public ApiResponse<String> minusCoin(Long studentId, int coin){
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new DataNotFoundException("Student not found")
        );

        student.setCoin(student.getCoin() - coin);
        studentRepository.save(student);
        return ApiResponse.success(null, "Success");
    }


    public ApiResponse<String> plusCoin(Long studentId, int coin){
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new DataNotFoundException("Student not found")
        );

        student.setCoin(student.getCoin() + coin);
        studentRepository.save(student);
        return ApiResponse.success(null, "Success");
    }
}
