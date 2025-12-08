package com.example.nazoratv2.mapper;

import com.example.nazoratv2.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.example.nazoratv2.dto.request.ReqGroup;
import com.example.nazoratv2.dto.response.ResGroup;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.repository.StudentRepository;

@Component
@RequiredArgsConstructor
public class GroupMapper {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public ReqGroup toDto(Group group) {
        return ReqGroup.builder()
                .id(group.getId())
                .name(group.getName())
                .startTime(group.getStartTime().toString())
                .endTime(group.getEndTime().toString())
                .roomName(group.getRoom().getName())
                .teacherName(group.getTeacher().getFullName())
                .roomId(group.getRoom().getId())
                .teacherId(group.getTeacher().getId())
                .categoryId(group.getCategory().getId())
                .categoryName(group.getCategory().getName())
                .weekDays(group.getWeekDays().stream().map(Enum::toString).toList())
                .students(studentRepository.findAllByGroup_id(group.getId()).stream()
                        .map(studentMapper::toStudentDTO).toList())
                .build();
    }



    public ResGroup toDtoRes(Group group) {
        return ResGroup.builder()
                .id(group.getId())
                .name(group.getName())
                .teacherName(group.getTeacher().getFullName())
                .studentCount(studentRepository.countByGroup_Id(group.getId()))
                .categoryName(group.getCategory().getName())
                .build();
    }
}
