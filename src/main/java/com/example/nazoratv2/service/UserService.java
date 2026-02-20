package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.*;
import com.example.nazoratv2.dto.request.ReqGroupDTO;
import com.example.nazoratv2.dto.response.*;
import com.example.nazoratv2.entity.Attendance;
import com.example.nazoratv2.entity.Mark;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.entity.enums.AttendaceEnum;
import com.example.nazoratv2.entity.enums.Role;
import com.example.nazoratv2.entity.enums.WeekDays;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.GroupMapper;
import com.example.nazoratv2.mapper.StudentMapper;
import com.example.nazoratv2.mapper.UserMapper;
import com.example.nazoratv2.repository.*;
import com.example.nazoratv2.security.CustomUserDetails;
import com.example.nazoratv2.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

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
    private final MarkRepository markRepository;
    private final AttendanceRepository attendanceRepository;

    public ApiResponse<?> getProfile(CustomUserDetails currentUser) {
        if (currentUser.getRole().equals("ROLE_STUDENT")) {
            Student student = currentUser.getStudent();
            return ApiResponse.success(studentMapper.toResponseUser(student), "Success");
        } else {
            User user = currentUser.getUser();
            return ApiResponse.success(mapper.toResponseUser(user), "success");
        }
    }

    public ApiResponse<String> update(CustomUserDetails current, UserDTO req) {

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
        if (user.getRole().name().equals("ROLE_TEACHER")) {
            int size = groupRepository.findAllByTeacherIdAndActiveTrue(user.getId()).size();
            if (size == 0) {
                user.setActive(false);
            } else {
                return ApiResponse.error("Cannot delete teacher");
            }
        } else if (user.getRole().name().equals("ROLE_PARENT")) {
            int size = studentRepository.findAllByParent_Id(user.getId()).size();
            if (size == 0) {
                user.setActive(false);
            } else {
                return ApiResponse.error("Cannot delete parent");
            }
        } else {
            user.setActive(false);
        }

        userRepository.save(user);
        return ApiResponse.success(null, "success");
    }

    public ApiResponse<UserResponse> getOneUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new DataNotFoundException("User topilmadi"));
        return ApiResponse.success(mapper.toResponseUser(user), "Success");
    }


    public ApiResponse<ResPageable> getAllUsersSearch(String name, String phone, Role role, int page, int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<User> users = userRepository.searchUser(name, phone, role, pageable);

        List<UserResponse> list = users.stream()
                .map(mapper::toResponseUser)
                .toList();

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


    public ApiResponse<ResTeacher> getOneTeacher(Long id) {
        User teacher = userRepository.findByIdAndActiveTrue(id).orElseThrow(
                () -> new DataNotFoundException("Teacher not found")
        );

        List<ResStudent> studentList = studentRepository.findAllByTeacher(teacher.getId())
                .stream().map(studentMapper::toStudentDTO).toList();
        List<ResGroupDTO> groupList = groupRepository.findAllByTeacherIdAndActiveTrue(teacher.getId())
                .stream().map(groupMapper::toRes).toList();

        return ApiResponse.success(mapper.resTeacher(teacher, studentList, groupList), "Success");
    }


    public ApiResponse<List<UserResponse>> getAllList(Role role) {
        List<UserResponse> list = userRepository.findAllByRole(role).stream().map(mapper::toResponseUser).toList();
        return ApiResponse.success(list, "success");
    }


    public ApiResponse<StudentStatsDTO> getStats(User parent, Long studentId) {
        Student student = getStudentForParent(studentId, parent);

        Double avg = markRepository.avgTotalScore(student.getId());
        if (avg == null) avg = 0.0;

        int groupsCount = (student.getGroup() != null) ? 1 : 0;


        StudentStatsDTO dto = StudentStatsDTO.builder()
                .averageGrade(round1(avg))
                .subjectsCount(groupsCount)
                .build();
        return ApiResponse.success(dto, "Success");
    }

    public ApiResponse<List<WeekMarkDTO>> getWeekMarks(User parent, Long studentId, LocalDate weekStart) {
        Student student = getStudentForParent(studentId, parent);

        LocalDate start = normalizeWeekStart(weekStart); // Monday
        LocalDate end = start.plusDays(4);               // Mon..Fri (7 kun kerak bo'lsa +6)

        List<Mark> marks = markRepository
                .findAllByStudentIdAndActiveTrueAndDateBetweenOrderByDateAsc(student.getId(), start, end);

        List<WeekMarkDTO> res = marks.stream()
                .map(m -> WeekMarkDTO.builder()
                        .day(toWeekDays(m.getDate().getDayOfWeek()))
                        .score(m.getTotalScore())
                        .category(m.getMarkCategoryStatus()) // YASHIL/SARIQ/QIZIL
                        .date(m.getDate())
                        .build())
                .toList();

        return ApiResponse.success(res, "Success");
    }

    public ApiResponse<List<WeekAttendanceDTO>> getWeekAttendance(User parent, Long studentId, LocalDate weekStart) {
        Student student = getStudentForParent(studentId, parent);

        LocalDate start = normalizeWeekStart(weekStart); // Monday
        LocalDate end = start.plusDays(5);               // Mon..Fri (7 kun kerak bo'lsa +6)

        List<Attendance> list =
                attendanceRepository.findAllByStudentIdAndDateBetween(student.getId(), start, end);

        // date -> present
        Map<LocalDate, Boolean> map = new HashMap<>();
        for (Attendance a : list) {
            boolean present = a.getStatus() == AttendaceEnum.KELDI;
            map.put(a.getDate(), present);
        }

        // har kuni chip chiqishi uchun (ma'lumot bo'lmasa false)
        List<WeekAttendanceDTO> res = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            LocalDate d = start.plusDays(i);
            res.add(WeekAttendanceDTO.builder()
                    .day(WeekDays.valueOf(d.getDayOfWeek().name()))
                    .present(map.getOrDefault(d, false))
                    .build());
        }

        return ApiResponse.success(res, "Success");
    }



    private Student getStudentForParent(Long studentId, User parent) {
        Student s = studentRepository.findById(studentId)
                .orElseThrow(() -> new DataNotFoundException("Student not found"));

        // parent check
        if (s.getParent() == null || !Objects.equals(s.getParent().getId(), parent.getId())) {
            throw new RuntimeException("Bu farzand sizga tegishli emas");
        }
        return s;
    }

    private LocalDate normalizeWeekStart(LocalDate weekStart) {
        if (weekStart == null) weekStart = LocalDate.now();
        return weekStart.with(DayOfWeek.MONDAY);
    }

    private WeekDays toWeekDays(DayOfWeek dow) {
        return WeekDays.valueOf(dow.name()); // MONDAY..SUNDAY mos
    }

    private double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }


}
