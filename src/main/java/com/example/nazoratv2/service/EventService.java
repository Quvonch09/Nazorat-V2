package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqEvent;
import com.example.nazoratv2.entity.Event;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.repository.EventRepository;
import com.example.nazoratv2.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final GroupRepository groupRepository;
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();


    public ApiResponse<String> addEvent(ReqEvent reqEvent){
        List<Group> groups = groupRepository.findAllById(reqEvent.getGroupIds());
        if (groups.isEmpty()) {
            return ApiResponse.error("Group not found");
        }

        LocalTime startTime = LocalTime.parse(reqEvent.getStartTime());
        LocalTime endTime = LocalTime.parse(reqEvent.getEndTime());

        Event event = Event.builder()
                .name(reqEvent.getName())
                .description(reqEvent.getDescription())
                .date(reqEvent.getDate())
                .startTime(startTime)
                .endTime(endTime)
                .groupList(groups)
                .build();
        eventRepository.save(event);

        sendToAll(reqEvent());
        return ApiResponse.success(null, "Success");
    }


    public ApiResponse<String> updateEvent(Long eventId,ReqEvent reqEvent){
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new DataNotFoundException("Event not found")
        );

        List<Group> groups = groupRepository.findAllById(reqEvent.getGroupIds());
        if (groups.isEmpty()) {
            return ApiResponse.error("Group not found");
        }

        LocalTime startTime = LocalTime.parse(reqEvent.getStartTime());
        LocalTime endTime = LocalTime.parse(reqEvent.getEndTime());

        event.setName(reqEvent.getName());
        event.setDescription(reqEvent.getDescription());
        event.setDate(reqEvent.getDate());
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setGroupList(groups);
        eventRepository.save(event);
        return ApiResponse.success(null, "Success");
    }


    public ApiResponse<String> deleteEvent(Long eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new DataNotFoundException("Event not found")
        );
        eventRepository.delete(event);
        return ApiResponse.success(null, "Success");
    }


    public SseEmitter addEmitter() {
        SseEmitter emitter = new SseEmitter(0L); // no timeout
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        emitters.add(emitter);
        return emitter;
    }

    public void sendToAll(Object data) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("events")
                        .data(data));
            } catch (Exception e) {
                // Exceptionni umuman ko'rsatmasdan emitterni olib tashlash
                emitters.remove(emitter);
            }
        }
    }





    public List<ReqEvent> reqEvent(){
        List<ReqEvent> reqEvents = new ArrayList<>();
        for (Event event : eventRepository.findAll()) {
            List<String> groupNames = event.getGroupList().stream().map(Group::getName).toList();
            ReqEvent reqEvent = ReqEvent.builder()
                    .id(event.getId())
                    .name(event.getName())
                    .description(event.getDescription())
                    .date(event.getDate())
                    .startTime(event.getStartTime().toString())
                    .endTime(event.getEndTime().toString())
                    .groupNames(groupNames)
                    .build();
            reqEvents.add(reqEvent);
        }
        return reqEvents;
    }


}
