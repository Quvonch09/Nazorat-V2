package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.RoomDTO;
import com.example.nazoratv2.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public RoomDTO roomDTO(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .build();
    }
}
