package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findAllByDeletedFalse(Pageable pageable);
    Optional<Question> findByIdAndDeletedFalse(Long id);

    @Query("""
        SELECT COALESCE(SUM(q.score), 0)
        FROM Question q
        WHERE q.category.id = :categoryId
          AND q.deleted = false
    """)
    Integer getTotalScoreByCategory(@Param("categoryId") Long categoryId);

    List<Question> findAllByCategoryIdAndDeletedFalse(Long categoryId);
}
