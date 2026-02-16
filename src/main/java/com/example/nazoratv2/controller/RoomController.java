package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.RoomDTO;
import com.example.nazoratv2.dto.request.ReqRoom;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.dto.response.ResRoom;
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


    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateRoom(@RequestBody ReqRoom reqRoom){
        return ResponseEntity.ok(roomService.updateRoom(reqRoom));
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


    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ResPageable>> searchRooms(@RequestParam(required = false) String name,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(roomService.searchRooms(name, page, size));
    }


    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<ResRoom>> getRoomById(@PathVariable Long roomId){
        return ResponseEntity.ok(roomService.getRoom(roomId));
    }
}
