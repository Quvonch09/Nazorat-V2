package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByUserId(Long userId);

    Optional<Task> findByIdAndUserId(Long taskId, Long userId);

    void deleteByIdAndUserId(Long taskId, Long userId);
}

