package com.example.nazoratv2.service;

import com.example.nazoratv2.entity.ActionEvent;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.entity.base.BaseEntity;
import com.example.nazoratv2.entity.enums.ActionType;
import com.example.nazoratv2.repository.ActionEventRepository;
import com.example.nazoratv2.security.CurrentActor;
import com.example.nazoratv2.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
@Service
@RequiredArgsConstructor
public class ActionEventService {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();


    private final SecurityUtil securityUtil;
    private final ActionEventRepository repository;

    public void handle(ActionType type, String description, Object result) {

        CurrentActor actor = securityUtil.getCurrentActor();

        ActionEvent event = new ActionEvent();
        event.setActionType(type);
        event.setDescription(description);
        event.setUserId(actor.getId());
        event.setUserRole(actor.getRole());
        event.setCreatedAt(LocalDateTime.now());

        repository.save(event);
        send(event);
    }



    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L); // cheksiz timeout

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    // Action bo‘lganda chaqiriladi
    public void send(ActionEvent event) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("system-action")
                                .data(event)
                );
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }
}
