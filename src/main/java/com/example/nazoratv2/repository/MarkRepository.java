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

    @Query("""
select m from Mark m
where m.active = true
  and (:groupId is null or m.student.group.id = :groupId)
  and (
       :keyword is null or :keyword = '' or
       lower(m.student.fullName) like lower(concat('%', :keyword, '%'))
  )
order by m.date desc, m.id desc
""")
    Page<Mark> findAllMark(@Param("keyword") String keyword,
                           @Param("groupId") Long groupId,
                           Pageable pageable);


    Page<Mark> findAllByCreatedByAndActiveTrue(String createdBy, Pageable pageable);




    Page<Mark> findAllByStudentIdAndActiveTrue(Long studentId, Pageable pageable);



    @Query("""
select m.student.id, m.student.fullName,
       sum(coalesce(m.totalScore, 0)),
       m.student.imgUrl
from Mark m
where m.active = true
group by m.student.id, m.student.fullName, m.student.imgUrl
order by sum(coalesce(m.totalScore, 0)) desc, m.student.fullName asc
""")
    List<Object[]> topAllStudentsByAvg(Pageable pageable);

    @Query("""
select m.student.id, m.student.fullName,
       sum(coalesce(m.totalScore, 0)),
       m.student.imgUrl
from Mark m
where m.active = true
  and m.student.group.id in :groupIds
group by m.student.id, m.student.fullName, m.student.imgUrl
order by sum(coalesce(m.totalScore, 0)) desc, m.student.fullName asc
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
