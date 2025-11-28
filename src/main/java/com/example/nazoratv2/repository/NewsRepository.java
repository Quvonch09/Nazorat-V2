package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News,Long> {
}
