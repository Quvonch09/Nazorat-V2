package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Mark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MarkRepository extends JpaRepository<Mark, Long> {

    @Query(value = """
    select m.* from mark m join student s on s.id = m.student_id join groups g on g.id = s.group_id where
    (:keyword IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) and
    (:keyword IS NULL OR LOWER(s.full_name) LIKE LOWER(CONCAT('%', :keyword, '%'))) and m.active = true and
    (:keyword IS NULL OR LOWER(s.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))) order by m.created_at desc
    """, nativeQuery = true)
    Page<Mark> findAllMark(@Param("keyword") String keyword, Pageable pageable);


    Page<Mark> findAllByCreatedByAndActiveTrue(String createdBy, Pageable pageable);




    Page<Mark> findAllByStudentIdAndActiveTrue(Long studentId, Pageable pageable);



    @Query("""
   select s.id, s.fullName, avg(m.totalScore), s.imgUrl
   from Mark m
   join m.student s
   where m.active = true
   group by s.id, s.fullName, s.imgUrl
   order by avg(m.totalScore) desc
""")
    List<Object[]> topAllStudentsByAvg(Pageable pageable);


    @Query("""
    select s.id, s.fullName,
           avg(m.totalScore),
           s.imgUrl
    from Mark m
    join m.student s
    join s.group g
    where g.id in :groupIds
    group by s.id, s.fullName, s.imgUrl
    order by avg(m.totalScore) desc
""")
    List<Object[]> topStudentsByAvgForGroups(@Param("groupIds") List<Long> groupIds, Pageable pageable);



    List<Mark> findAllByStudentIdAndActiveTrueAndDateBetweenOrderByDateAsc(
            Long studentId, LocalDate start, LocalDate end
    );

    @Query("""
        select avg(m.totalScore * 1.0)
        from Mark m
        where m.active = true and m.student.id = :studentId
    """)
    Double avgTotalScore(@Param("studentId") Long studentId);

    @Query("""
        select count(m)
        from Mark m
        where m.active = true and m.student.id = :studentId
    """)
    Long countMarks(@Param("studentId") Long studentId);


}
