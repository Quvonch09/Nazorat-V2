package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.RoomDTO;
import com.example.nazoratv2.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> saveRoom(@RequestBody RoomDTO roomDTO){
        return ResponseEntity.ok(roomService.saveRoom(roomDTO));
    }


    @PutMapping("/{roomId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateRoom(@PathVariable Long roomId, @RequestBody RoomDTO roomDTO){
        return ResponseEntity.ok(roomService.updateRoom(roomId, roomDTO));
    }


    @DeleteMapping("/{roomId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteRoom(@PathVariable Long roomId){
        return ResponseEntity.ok(roomService.deleteRoom(roomId));
    }



    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomDTO>>> getAllRooms(){
        return ResponseEntity.ok(roomService.getAllRooms());
    }


    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomDTO>> getRoomById(@PathVariable Long roomId){
        return ResponseEntity.ok(roomService.getRoom(roomId));
    }
}
