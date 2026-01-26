package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.UserDTO;
import com.example.nazoratv2.dto.request.ReqGroupDTO;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.dto.response.ResStudent;
import com.example.nazoratv2.dto.response.ResTeacher;
import com.example.nazoratv2.dto.response.UserResponse;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.GroupMapper;
import com.example.nazoratv2.mapper.StudentMapper;
import com.example.nazoratv2.mapper.UserMapper;
import com.example.nazoratv2.repository.GroupRepository;
import com.example.nazoratv2.repository.StudentRepository;
import com.example.nazoratv2.repository.UserRepository;
import com.example.nazoratv2.security.CustomUserDetails;
import com.example.nazoratv2.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final JwtService jwtService;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final GroupMapper groupMapper;
    private final StudentMapper studentMapper;

    public ApiResponse<UserResponse> getProfile(CustomUserDetails currentUser) {
        User user = currentUser.getUser();
        return ApiResponse.success(mapper.toResponseUser(user),"success");
    }

    public ApiResponse<String> update(CustomUserDetails current , UserDTO req) {

        User currentUser = current.getUser();
        Long targetId = req.getId();

        User targetUser;

        if (targetId == null) {
            targetUser = currentUser;
        } else {
            if (targetId.equals(currentUser.getId())) {
                targetUser = currentUser;
            } else {
                if (!currentUser.getRole().name().equals("ROLE_ADMIN") &&
                        !currentUser.getRole().name().equals("ROLE_SUPER_ADMIN")) {
                    return ApiResponse.error("Siz boshqa userni update qila olmaysiz!");
                }
                targetUser = userRepository.findById(targetId)
                        .orElseThrow(() -> new DataNotFoundException("User topilmadi"));
            }
        }
        String oldPhone = targetUser.getPhone();
        String newPhone = req.getPhone();

        if (req.getFullName() != null)
            targetUser.setFullName(req.getFullName());

        if (req.getImageUrl() != null)
            targetUser.setImageUrl(req.getImageUrl());

        String newToken = null;

        if (newPhone != null && !newPhone.equals(oldPhone)) {
            targetUser.setPhone(newPhone);
            newToken = jwtService.generateToken(newPhone, targetUser.getRole().name());
        }

        userRepository.save(targetUser);

        return ApiResponse.success(newToken, "success");

    }

    public ApiResponse<String> deleteById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("user not found"));
        user.setActive(false);
        userRepository.save(user);
        return ApiResponse.success(null,"success");
    }

    public ApiResponse<UserResponse> getOneUser(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new DataNotFoundException("User topilmadi"));
        return ApiResponse.success(mapper.toResponseUser(user), "Success");
    }


    public ApiResponse<ResPageable> getAllUsersSearch(String name, String phone, int page, int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<User> users = userRepository.searchUser(name, phone, pageable);

        List<UserResponse> list = users.stream().map(mapper::toResponseUser).toList();;

        if (users.isEmpty()) {
            return ApiResponse.error("Foydalanuvchilar topilmadi");
        }

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(users.getTotalElements())
                .totalPage(users.getTotalPages())
                .body(list)
                .build();

        return ApiResponse.success(resPageable, "success");
    }



    public ApiResponse<ResTeacher> getOneTeacher(Long id){
        User teacher = userRepository.findByIdAndActiveTrue(id).orElseThrow(
                () -> new DataNotFoundException("Teacher not found")
        );

        List<ResStudent> studentList = studentRepository.findAllByTeacher(teacher.getId())
                .stream().map(studentMapper::toStudentDTO).toList();
        List<ReqGroupDTO> groupList = groupRepository.findAllByTeacherId(teacher.getId())
                .stream().map(groupMapper::toReq).toList();

        return ApiResponse.success(mapper.resTeacher(teacher,studentList,groupList), "Success");
    }

}
