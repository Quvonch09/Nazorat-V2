package com.example.nazoratv2.controller;

import com.example.nazoratv2.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;


    @PostMapping("/{userId}")
    public Task create(@PathVariable Long userId,
                       @RequestBody Task task) {
        return taskService.createTask(task, userId);
    }


    @GetMapping("/{userId}")
    public List<com.example.nazoratv2.entity.Task> getAll(@PathVariable Long userId) {
        return taskService.getAllTasks(userId);
    }


    @GetMapping("/{userId}/{taskId}")
    public com.example.nazoratv2.entity.Task getOne(@PathVariable Long userId,
                                                    @PathVariable Long taskId) {
        return taskService.getOneTask(taskId, userId);
    }


    @PutMapping("/{userId}/{taskId}")
    public Task update(@PathVariable Long userId,
                       @PathVariable Long taskId,
                       @RequestBody String task) {
        return taskService.updateTask(taskId, userId, task);
    }

    // DELETE
    @DeleteMapping("/{userId}/{taskId}")
    public Task delete(@PathVariable Long userId,
                       @PathVariable Long taskId) {
        taskService.deleteTask(taskId, userId);
        return null;
    }

}
