package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.GroupScheduleDTO;
import com.example.nazoratv2.dto.ScheduleResponseDTO;
import com.example.nazoratv2.dto.dashboard.DashboardDTO;
import com.example.nazoratv2.dto.dashboard.TeacherDashboard;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.entity.Room;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.entity.enums.GroupEnum;
import com.example.nazoratv2.entity.enums.Role;
import com.example.nazoratv2.entity.enums.WeekDays;
import com.example.nazoratv2.repository.*;
import com.example.nazoratv2.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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


    public ApiResponse<TeacherDashboard> getTeacherDashboard(CustomUserDetails customUserDetails) {
        DayOfWeek day = LocalDate.now().getDayOfWeek();

        WeekDays today = WeekDays.valueOf(day.name());

        User teacher = customUserDetails.getUser();
        int student = studentRepository.findAllByTeacher(teacher.getId()).size();
        int group = groupRepository.findAllByTeacherIdAndActiveTrue(teacher.getId()).size();
        Long countLesson = groupRepository.getTodayLessonHoursByTeacher(today.name(), teacher.getId());
        TeacherDashboard teacherDashboard = TeacherDashboard.builder()
                .countLesson(countLesson)
                .groupCount(group)
                .studentCount(student)
                .build();
        return ApiResponse.success(teacherDashboard, "Success");
    }


    public ApiResponse<List<ScheduleResponseDTO>> getSchedule(CustomUserDetails customUserDetails,
                                                              GroupEnum groupEnum) {

        List<ScheduleResponseDTO> result = new ArrayList<>();
        List<GroupScheduleDTO> groupDTOs =  new ArrayList<>();

        for (Room room : roomRepository.findAllByActiveTrue()) {
            List<Group> groups;
            if (customUserDetails.getRole().equals(Role.ROLE_TEACHER.name())) {
                groups = groupRepository.findAllByRoomIdAndActiveTrueAndTeacher_Id(room.getId(),customUserDetails.getUser().getId());
            } else {
                groups = groupRepository.findAllByRoomIdAndActiveTrue(room.getId());
            }
            if (groupEnum != null) {
                if (groupEnum.equals(GroupEnum.TOQ_KUNLAR)){
                    groupDTOs = groups.stream()
                            .map(this::groupToDTO)
                            .filter(group -> group.getWeekDays().contains(WeekDays.MONDAY))
                            .toList();
                } else if (groupEnum.equals(GroupEnum.JUFT_KUNLAR)){
                    groupDTOs = groups.stream()
                            .map(this::groupToDTO)
                            .filter(group -> group.getWeekDays().contains(WeekDays.TUESDAY))
                            .toList();
                }
            } else {
                groupDTOs = groups.stream()
                        .map(this::groupToDTO)
                        .toList();
            }

            result.add(
                    ScheduleResponseDTO.builder()
                            .roomName(room.getName())
                            .groups(groupDTOs)
                            .build()
            );
        }

        return ApiResponse.success(result, "Success");
    }

    private GroupScheduleDTO groupToDTO(Group group) {
        return GroupScheduleDTO.builder()
                .groupName(group.getName())
                .teacherName(group.getTeacher().getFullName())
                .startTime(group.getStartTime().toString())
                .endTime(group.getEndTime().toString())
                .weekDays(group.getWeekDays())
                .build();
    }

}
