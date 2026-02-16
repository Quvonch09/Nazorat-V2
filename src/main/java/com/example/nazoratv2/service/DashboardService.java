package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.dashboard.DashboardDTO;
import com.example.nazoratv2.entity.enums.Role;
import com.example.nazoratv2.entity.enums.WeekDays;
import com.example.nazoratv2.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;

    public ApiResponse<DashboardDTO> getDashboard() {
        DayOfWeek day = LocalDate.now().getDayOfWeek();

        WeekDays today = WeekDays.valueOf(day.name());

        long category = categoryRepository.countCategoryByActiveTrue();
        long teacher = userRepository.countUserByActiveTrueAndRole(Role.ROLE_TEACHER);
        long admin = userRepository.countUserByActiveTrueAndRole(Role.ROLE_ADMIN);
        long group = groupRepository.countAllByActiveTrue();
        long countLesson = groupRepository.getTodayLessonHours(today.name());
        long room = roomRepository.countRoomByActiveTrue();
        long student = studentRepository.countByActiveTrue();
        DashboardDTO dashboardDTO = DashboardDTO.builder()
                .countCategory(category)
                .countEmployees(teacher+admin)
                .countGroups(group)
                .countLessons(countLesson)
                .countRooms(room)
                .countStudents(student)
                .build();
        return ApiResponse.success(dashboardDTO, "Success");
    }
}
