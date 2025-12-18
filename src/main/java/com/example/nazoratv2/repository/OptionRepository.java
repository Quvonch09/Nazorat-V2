package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    Page<Option> findAllByQuestionIdAndDeletedFalse(Long questionId, Pageable pageable);
    Optional<Option> findByIdAndDeletedFalse(Long id);
}
