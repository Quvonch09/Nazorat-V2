package com.example.nazoratv2.service;

import com.example.nazoratv2.configuration.TrackAction;
import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.DayStat;
import com.example.nazoratv2.dto.RoomDTO;
import com.example.nazoratv2.dto.request.ReqGroupDTO;
import com.example.nazoratv2.dto.request.ReqRoom;
import com.example.nazoratv2.dto.response.ResRoom;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.entity.Room;
import com.example.nazoratv2.entity.enums.ActionType;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.GroupMapper;
import com.example.nazoratv2.mapper.RoomMapper;
import com.example.nazoratv2.repository.GroupRepository;
import com.example.nazoratv2.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    @TrackAction(
            type = ActionType.ROOM_CREATED,
            description = "Xona yaratildi"
    )
    public ApiResponse<String> saveRoom(RoomDTO roomDTO){
        if (roomRepository.existsByName(roomDTO.getName())) {
            return ApiResponse.error("Name already exists");
        }

        Room room = Room.builder()
                .name(roomDTO.getName())

                .build();
        roomRepository.save(room);
        return ApiResponse.success(null, "Success");
    }


    @TrackAction(
            type = ActionType.ROOM_UPDATED,
            description = "Xona yaratildi"
    )
    public ApiResponse<String> updateRoom(ReqRoom reqRoom){
        Room room = roomRepository.findById(reqRoom.getId()).orElseThrow(
                () -> new DataNotFoundException("Room not found")
        );

        room.setName(reqRoom.getName());
        roomRepository.save(room);
        return ApiResponse.success(null, "Success");
    }


    public ApiResponse<String> deleteRoom(Long id){
        Room room = roomRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Room not found")
        );

        int size = groupRepository.findAllByRoomIdAndActiveTrue(room.getId()).size();
        if (size == 0){
            room.setActive(false);
        } else {
            return ApiResponse.error("Cannot delete room");
        }

        roomRepository.save(room);
        return ApiResponse.success(null, "Success");
    }



    public ApiResponse<ResRoom> getRoom(Long id){
        Room room = roomRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Room not found")
        );
        List<Group> groups = groupRepository.findAllByRoomIdAndActiveTrue(room.getId());

        List<ReqGroupDTO> schedules = groups.stream().map(groupMapper::toReq).toList();

        List<DayStat> weeklyStats = RoomScheduleStats.buildWeeklyStats(groups);


        return ApiResponse.success(roomMapper.resRoom(room, schedules,weeklyStats), "Success");
    }


    public ApiResponse<List<RoomDTO>> getAllRooms(){
        List<RoomDTO> rooms = roomRepository.findAll().stream().map(roomMapper::roomDTO).toList();
        return ApiResponse.success(rooms, "Success");
    }
}
