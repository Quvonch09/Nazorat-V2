package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Mark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MarkRepository extends JpaRepository<Mark, Long> {

    @Query(value = """
    select m.* from mark m join student s on s.id = m.student_id join groups g on g.id = s.group_id where
    (:keyword IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) and
    (:keyword IS NULL OR LOWER(s.full_name) LIKE LOWER(CONCAT('%', :keyword, '%'))) and
    (:keyword IS NULL OR LOWER(s.phone_number) LIKE LOWER(CONCAT('%', :keyword, '%'))) order by m.created_at desc
    """, nativeQuery = true)
    Page<Mark> findAllMark(@Param("keyword") String keyword, Pageable pageable);


    Page<Mark> findAllByCreatedBy(String teacherName, Pageable pageable);

    Page<Mark> findAllByStudentId(Long studentId, Pageable pageable);

}
