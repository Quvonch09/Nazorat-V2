package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.ActionEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionEventRepository extends JpaRepository<ActionEvent, Long> {
}
