package com.example.nazoratv2.service;

import com.example.nazoratv2.configuration.TrackAction;
import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.AttendanceDto;
import com.example.nazoratv2.entity.Attendance;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.enums.ActionType;
import com.example.nazoratv2.entity.enums.AttendaceEnum;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.AttendanceMapper;
import com.example.nazoratv2.repository.AttendanceRepository;
import com.example.nazoratv2.repository.GroupRepository;
import com.example.nazoratv2.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final AttendanceMapper attendanceMapper;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();



    @TrackAction(
            type = ActionType.ATTENDANCE_TAKEN,
            description = "Davomat olindi"
    )
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

        sendToAll(getAllAttendanceByGroup(group.getId()));
        return ApiResponse.success(null, "Success");
    }


    public ApiResponse<String> deleteAttendance(Long attendanceId){
        Attendance attendance = attendanceRepository.findById(attendanceId).orElseThrow(
                () -> new DataNotFoundException("Attendance not found")
        );
        attendanceRepository.delete(attendance);
        return ApiResponse.success(null, "Success");
    }


    public ApiResponse<AttendanceDto> getAttendance(Long attendanceId){
        Attendance attendance = attendanceRepository.findById(attendanceId).orElseThrow(
                () -> new DataNotFoundException("Attendance not found")
        );

        return ApiResponse.success(attendanceMapper.toDto(attendance), "Success");
    }


    public List<AttendanceDto> getAllAttendanceByGroup(Long groupId) {
        return attendanceRepository
                .findAllByGroupIdOrderByCreatedAtDesc(groupId)
                .stream()
                .map(attendanceMapper::toDto)
                .toList();
    }

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L); // cheksiz timeout

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    // Barcha ulangan clientlarga yuborish
    public void sendToAll(Object data) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("attendance")
                                .data(data)
                );
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }













}
