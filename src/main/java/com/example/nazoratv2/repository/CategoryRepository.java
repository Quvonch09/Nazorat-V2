package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Category;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {


    @Query(value = """
    select c.* from category c where
    (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) OR
    (:name IS NULL OR LOWER(c.description) LIKE LOWER(CONCAT('%', :name, '%'))) and c.active = true 
    order by c.created_at desc
    """, nativeQuery = true)
    Page<Category> searchCategory(@Param("name") String name, Pageable pageable);

    long countCategoryByActiveTrue();

}