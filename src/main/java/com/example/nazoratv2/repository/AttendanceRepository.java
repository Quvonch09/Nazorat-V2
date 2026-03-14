package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Attendance;
import com.example.nazoratv2.entity.enums.AttendaceEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Attendance findByStudentIdAndDate(Long studentId, LocalDate date);

    List<Attendance> findAllByGroupIdOrderByCreatedAtDesc(Long groupId);

    List<Attendance> findAllByStudentIdAndDateBetween(Long studentId, LocalDate start, LocalDate end);

    Integer countByStudentId(Long studentId);

    Integer countByStudentIdAndStatus(Long studentId, AttendaceEnum status);
}
