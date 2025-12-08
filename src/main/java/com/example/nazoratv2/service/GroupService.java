package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqGroup;
import com.example.nazoratv2.dto.response.ResGroup;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.entity.Category;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.entity.Room;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.entity.enums.Role;
import com.example.nazoratv2.entity.enums.WeekDays;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.GroupMapper;
import com.example.nazoratv2.repository.CategoryRepository;
import com.example.nazoratv2.repository.GroupRepository;
import com.example.nazoratv2.repository.RoomRepository;
import com.example.nazoratv2.repository.UserRepository;
import com.example.nazoratv2.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final GroupMapper groupMapper;
    private final CategoryRepository categoryRepository;


    public ApiResponse<String> saveGroup(ReqGroup reqGroup){
        User teacher = userRepository.findById(reqGroup.getTeacherId()).orElseThrow(
                () -> new DataNotFoundException("Teacher not found")
        );

        Room room = roomRepository.findById(reqGroup.getRoomId()).orElseThrow(
                () -> new DataNotFoundException("Room not found")
        );

        Category category = categoryRepository.findById(reqGroup.getCategoryId()).orElseThrow(
                () -> new DataNotFoundException("Category not found")
        );

        LocalTime startTime = LocalTime.parse(reqGroup.getStartTime());
        LocalTime endTime = LocalTime.parse(reqGroup.getEndTime());

        if (groupRepository.existsByGroup(reqGroup.getWeekDays(),room.getId(),startTime, endTime)) {
            return ApiResponse.error("There is no room for the group at this time");
        }


        List<WeekDays> weekdays = reqGroup.getWeekDays().stream().map(WeekDays::valueOf).toList();

        Group group = Group.builder()
                .name(reqGroup.getName())
                .startTime(startTime)
                .endTime(endTime)
                .teacher(teacher)
                .room(room)
                .weekDays(weekdays)
                .category(category)
                .build();
        groupRepository.save(group);
        return ApiResponse.success(null, "Group successfully saved");
    }




    public ApiResponse<String> updateGroup(Long groupId, ReqGroup reqGroup){
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new DataNotFoundException("Group not found")
        );

        LocalTime startTime = LocalTime.parse(reqGroup.getStartTime());
        LocalTime endTime = LocalTime.parse(reqGroup.getEndTime());

        if (groupRepository.existsByGroupForUpdate(reqGroup.getWeekDays(),
                reqGroup.getRoomId(), startTime, endTime, group.getId())) {
            return ApiResponse.error("There is no room for the group at this time");
        }

        User teacher = userRepository.findById(reqGroup.getTeacherId()).orElseThrow(
                () -> new DataNotFoundException("Teacher not found")
        );

        Category category = categoryRepository.findById(reqGroup.getCategoryId()).orElseThrow(
                () -> new DataNotFoundException("Category not found")
        );

        Room room = roomRepository.findById(reqGroup.getRoomId()).orElseThrow(
                () -> new DataNotFoundException("Room not found")
        );

        List<WeekDays> weekdays = reqGroup.getWeekDays().stream().map(WeekDays::valueOf).toList();

        group.setStartTime(startTime);
        group.setEndTime(endTime);
        group.setTeacher(teacher);
        group.setRoom(room);
        group.setName(reqGroup.getName());
        group.setWeekDays(weekdays);
        group.setCategory(category);
        groupRepository.save(group);
        return ApiResponse.success(null, "Group successfully updated");
    }



    public ApiResponse<String> deleteGroup(Long groupId){
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new DataNotFoundException("Group not found")
        );
        groupRepository.delete(group);
        return ApiResponse.success(null, "Group successfully deleted");
    }

    public ApiResponse<ReqGroup> getGroupById(Long id){
        Group group = groupRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Group not found")
        );
        return ApiResponse.success(groupMapper.toDto(group), "Success");
    }



    public ApiResponse<ResPageable> searchGroup(CustomUserDetails userDetails, String name, String teacherName,
                                                String roomName, int page, int size){
        Page<Group> groups;

        if (userDetails.getRole().equals(Role.ROLE_TEACHER.name())){
            groups = groupRepository.searchByGroup(name, userDetails.getFullName(), roomName, PageRequest.of(page, size));
        } else {
            groups = groupRepository.searchByGroup(name, teacherName, roomName, PageRequest.of(page, size));
        }

        if(groups.getTotalElements() == 0){
            return ApiResponse.error("Group not found");
        }

        List<ResGroup> list = groups.stream().map(groupMapper::toDtoRes).toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(groups.getTotalElements())
                .totalPage(groups.getTotalPages())
                .body(list)
                .build();
        return ApiResponse.success(resPageable, "Success");
    }


    public ApiResponse<List<ResGroup>> getAllGroup(){
        List<Group> groups = groupRepository.findAll();
        List<ResGroup> list = groups.stream().map(groupMapper::toDtoRes).toList();
        return ApiResponse.success(list, "Success");
    }


    public ApiResponse<List<LocalDate>> getLessonDaysForMonth(Long groupId, YearMonth yearMonth) {

        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new DataNotFoundException("Group not found")
        );

        // Guruhning dars kunlari
        List<WeekDays> lessonDays = group.getWeekDays();

        // Bir oylik boshlanish va tugash sanasi
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<LocalDate> lessonDates = new ArrayList<>();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            // Hafta kuni guruh jadvalidagi kunlar bilan mos bo'lsa
            if (lessonDays.contains(WeekDays.valueOf(date.getDayOfWeek().name()))) {
                lessonDates.add(date);
            }
        }

        return ApiResponse.success(lessonDates, "Success");
    }

}
