package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Category;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    List<Category> findAllByActive(Boolean active);

    List<Category> findByNameContainingIgnoreCase(String name);

    List<Category> findByDuration(Integer duration);

    List<Category> findByDurationIn(List<Integer> durations);



}