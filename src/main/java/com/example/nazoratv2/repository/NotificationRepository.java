package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByParentId(Long parentId);
    List<Notification> findAllByStudentId(Long studentId);

    @Query(value = """
    select count(*) from notification n
                    where n.parent_id = ?1 and n.is_read = false
    """, nativeQuery = true)
    long countByParentIdAndReadFalse(Long parentId);

    @Query(value = """
    select count(*) from notification n
                    where n.student_id = ?1 and n.is_read = false
    """, nativeQuery = true)
    long countByStudentIdAndReadFalse(Long studentId);

}
