package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByName(String name);

    @Query(value = """
    select r.* from room r where 
    (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) order by r.created_at desc
    """, nativeQuery = true)
    Page<Room> searchRooms(@Param("name") String name, Pageable pageable);

    long countRoomByActiveTrue();

    List<Room> findAllByActiveTrue();
}
