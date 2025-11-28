package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqStudent;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.dto.response.ResStudent;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.StudentMapper;
import com.example.nazoratv2.repository.StudentRepository;
import com.example.nazoratv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

}
