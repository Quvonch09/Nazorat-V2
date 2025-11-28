package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    List<Category> findAllByActive(Boolean active);

    List<Category> findByNameContainingIgnoreCase(String name);

    List<Category> findByDuration(Integer duration);

    List<Category> findByDurationIn(List<Integer> durations);

    List<Category> findByActiveAndNameContainingIgnoreCase(Boolean active, String name);

}
