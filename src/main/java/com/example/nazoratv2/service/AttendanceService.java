package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.AttendanceDto;
import com.example.nazoratv2.entity.Attendance;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.enums.AttendaceEnum;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.repository.AttendanceRepository;
import com.example.nazoratv2.repository.GroupRepository;
import com.example.nazoratv2.repository.StudentRepository;
import com.example.nazoratv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;


    public ApiResponse<String> saveAttendance(Long groupId, List<AttendanceDto> attendanceDtoList){
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new DataNotFoundException("Group not found")
        );

        for (AttendanceDto attendanceDto : attendanceDtoList) {
            Student student = studentRepository.findById(attendanceDto.getStudentId()).orElseThrow(
                    () -> new DataNotFoundException("Student not found")
            );

            if (attendanceRepository.findByStudentIdAndDate(student.getId(), attendanceDto.getDate()) == null) {
                Attendance attendance = Attendance.builder()
                        .group(group)
                        .student(student)
                        .date(attendanceDto.getDate())
                        .status(attendanceDto.getStatus())
                        .description(attendanceDto.getStatus().equals(AttendaceEnum.KELDI) ?
                                null : attendanceDto.getDescription())
                        .build();
                attendanceRepository.save(attendance);
            } else {
                Attendance attendance =
                        attendanceRepository.findByStudentIdAndDate(student.getId(), attendanceDto.getDate());
                attendance.setStatus(attendanceDto.getStatus());
                attendance.setDescription(attendanceDto.getStatus().equals(AttendaceEnum.KELDI) ?
                        null : attendanceDto.getDescription());
                attendanceRepository.save(attendance);
            }

        }

        return ApiResponse.success(null, "Success");
    }
}
