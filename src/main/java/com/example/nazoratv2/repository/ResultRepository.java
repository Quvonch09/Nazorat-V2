package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    int countByStudentIdAndCategoryId(Long studentId, Long categoryId);

    Optional<Result> findByIdAndDeletedFalse(Long id);

    Page<Result> findAllByStudentIdAndDeletedFalse(Long studentId, Pageable pageable);

    Page<Result> findAllByCategoryIdAndDeletedFalse(Long categoryId, Pageable pageable);

    Page<Result> findAllByDeletedFalse(Pageable pageable);

}
