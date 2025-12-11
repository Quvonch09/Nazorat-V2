package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqEvent;
import com.example.nazoratv2.entity.Event;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.repository.EventRepository;
import com.example.nazoratv2.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventRepository eventRepository;

    @PostMapping
    @Operation(summary = "Event saqlash")
    public ResponseEntity<ApiResponse<String>> saveEvent(@RequestBody ReqEvent reqEvent){
        return ResponseEntity.ok(eventService.addEvent(reqEvent));
    }


    @PutMapping("/{eventId}")
    @Operation(summary = "Eventni update qilish")
    public ResponseEntity<ApiResponse<String>> updateEvent(@PathVariable Long eventId, @RequestBody ReqEvent reqEvent){
        return ResponseEntity.ok(eventService.updateEvent(eventId, reqEvent));
    }


    @DeleteMapping("/{eventId}")
    @Operation(summary = "Eventni uchirish")
    public ResponseEntity<ApiResponse<String>> deleteEvent(@PathVariable Long eventId){
        return ResponseEntity.ok(eventService.deleteEvent(eventId));
    }


    @GetMapping("/stream")
    public SseEmitter stream() {
        SseEmitter emitter = eventService.addEmitter();

        try {
            emitter.send(SseEmitter.event()
                    .name("events")
                    .data(eventService.reqEvent()));
        } catch (Exception e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

}
