package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {


        @Query("""
    SELECT t FROM Task t
    WHERE (:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')))
    AND (:userName IS NULL OR LOWER(t.user.fullName) LIKE LOWER(CONCAT('%', :userName, '%')))
    """)
        Page<Task> searchTasks(
                @Param("title") String title,
                @Param("userName") String userName,
                Pageable pageable
        );
    }

