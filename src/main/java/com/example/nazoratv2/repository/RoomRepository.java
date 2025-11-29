package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByName(String name);
}
