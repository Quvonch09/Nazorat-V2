package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.RoomDTO;
import com.example.nazoratv2.entity.Room;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.RoomMapper;
import com.example.nazoratv2.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public ApiResponse<String> saveRoom(RoomDTO roomDTO){
        if (roomRepository.existsByName(roomDTO.getName())) {
            return ApiResponse.error("Name already exists");
        }

        Room room = new Room(roomDTO.getName());
        roomRepository.save(room);
        return ApiResponse.success(null, "Success");
    }


    public ApiResponse<String> updateRoom(Long id,RoomDTO roomDTO){
        Room room = roomRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Room not found")
        );

        room.setName(roomDTO.getName());
        roomRepository.save(room);
        return ApiResponse.success(null, "Success");
    }


    public ApiResponse<String> deleteRoom(Long id){
        Room room = roomRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Room not found")
        );

        roomRepository.delete(room);
        return ApiResponse.success(null, "Success");
    }



    public ApiResponse<RoomDTO> getRoom(Long id){
        Room room = roomRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Room not found")
        );

        return ApiResponse.success(roomMapper.roomDTO(room), "Success");
    }


    public ApiResponse<List<RoomDTO>> getAllRooms(){
        List<RoomDTO> rooms = roomRepository.findAll().stream().map(roomMapper::roomDTO).toList();
        return ApiResponse.success(rooms, "Success");
    }
}
