package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query(value = """
    SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
    FROM groups g
    JOIN groups_week_days w ON g.id = w.groups_id
    WHERE g.room_id = :roomId
      AND w.week_days IN (:weekdays)
      AND (:startTime < g.end_time AND :endTime > g.start_time) AND g.active = true
""", nativeQuery = true)
    boolean existsByGroup(@Param("weekdays") List<String> weekdays,
                          @Param("roomId") Long roomId,
                          @Param("startTime") LocalTime startTime,
                          @Param("endTime") LocalTime endTime);



    @Query(value = """
    SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
    FROM groups g
    JOIN groups_week_days w ON g.id = w.groups_id
    WHERE g.room_id = :roomId
      AND w.week_days IN (:weekdays)
      AND (:startTime < g.end_time AND :endTime > g.start_time)
      AND g.id <> :groupId AND g.active = true;
""", nativeQuery = true)
    boolean existsByGroupForUpdate(@Param("weekdays") List<String> weekdays,
                                   @Param("roomId") Long roomId,
                                   @Param("startTime") LocalTime startTime,
                                   @Param("endTime") LocalTime endTime,
                                   @Param("groupId") Long groupId);


    @Query(value = """
           select g.* from groups g join users u on u.id = g.teacher_id join room r on g.room_id = r.id where
            (:name IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%',:name,'%'))) and
            (:teacherName IS NULL OR LOWER(u.full_name)LIKE LOWER(CONCAT('%',:teacherName,'%'))) and g.active = true and
            (:roomName IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%',:roomName,'%'))) order by g.created_at desc
    """, nativeQuery = true)
    Page<Group> searchByGroup(@Param("name") String name,
                              @Param("teacherName") String teacherName,
                              @Param("roomName") String roomName, Pageable pageable);

    List<Group> findAllByTeacherIdAndActiveTrue(Long teacherId);

    List<Group> findAllByRoomIdAndActiveTrue(Long roomId);
    List<Group> findAllByRoomIdAndActiveTrueAndTeacher_Id(Long roomId, Long teacherId);

    List<Group> findAllByCategoryIdAndActiveTrue(Long roomId);

    List<Group> findAllByActiveTrue();

    long countAllByActiveTrue();

    Optional<Group> findByIdAndActiveTrue(Long id);

    @Query(value = """
   SELECT COALESCE(SUM(
       EXTRACT(EPOCH FROM (g.end_time - g.start_time)) / 3600
   ), 0)
   FROM groups g
   JOIN groups_week_days w ON g.id = w.groups_id
   WHERE w.week_days = :today
""", nativeQuery = true)
    Long getTodayLessonHours(@Param("today") String today);

    @Query(value = """
   SELECT COALESCE(SUM(
       EXTRACT(EPOCH FROM (g.end_time - g.start_time)) / 3600
   ), 0)
   FROM groups g
   JOIN groups_week_days w ON g.id = w.groups_id
   WHERE w.week_days = :today and g.teacher_id = :teacherId
""", nativeQuery = true)
    Long getTodayLessonHoursByTeacher(@Param("today") String today,
                                      @Param("teacherId") Long teacherId);

    @Query("select g.id from groups g where g.teacher.id = :teacherId and g.active = true")
    List<Long> findIdsByTeacherId(Long teacherId);


//    Optional<Group> findByStudentIdAndActiveTrue(Long studentId);


}
