package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.DayStat;
import com.example.nazoratv2.dto.RoomDTO;
import com.example.nazoratv2.dto.request.ReqGroupDTO;
import com.example.nazoratv2.dto.response.ResGroupDTO;
import com.example.nazoratv2.dto.response.ResRoom;
import com.example.nazoratv2.entity.Room;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomMapper {

    public RoomDTO roomDTO(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .build();
    }


    public ResRoom resRoom(Room room, List<ResGroupDTO> schedules, List<DayStat> weeklyStats) {
        return ResRoom.builder()
                .id(room.getId())
                .name(room.getName())
                .schedules(schedules)
                .weeklyStats(weeklyStats)
                .build();
    }
}
