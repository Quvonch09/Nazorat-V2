package com.example.nazoratv2.controller;

import com.example.nazoratv2.repository.ActionEventRepository;
import com.example.nazoratv2.service.ActionEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/action")
@RequiredArgsConstructor
public class ActionEventController {
    private final ActionEventService eventService;
    private final ActionEventRepository actionEventRepository;

    @GetMapping
    public SseEmitter sseEmitter(){
        SseEmitter emitter = eventService.subscribe();

        try {
            // Dastlabki ma'lumotni yuboramiz
            emitter.send(
                    SseEmitter.event()
                            .name("system-action")
                            .data(actionEventRepository.findAll())
            );
        } catch (Exception e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }
}
