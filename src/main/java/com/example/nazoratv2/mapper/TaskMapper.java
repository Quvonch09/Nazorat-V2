package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.request.ReqTask;
import com.example.nazoratv2.dto.response.ResTask;
import com.example.nazoratv2.entity.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TaskMapper {
    public Task toEntity(ReqTask req) {
        if (req == null) return null;

        return Task.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .deadline(String.valueOf(req.getDeadline()))
                .build();
    }

    // Task → ResTask
    public ResTask toDto(Task task) {
        if (task == null) return null;

        return ResTask.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .deadline(LocalDate.parse(task.getDeadline()))
                .build();
    }
}
