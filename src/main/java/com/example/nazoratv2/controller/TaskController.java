package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqTask;
import com.example.nazoratv2.dto.response.ResTask;
import com.example.nazoratv2.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiResponse<ResTask>> createTask(@RequestBody ReqTask req) {
        return ResponseEntity.ok(taskService.createTask(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResTask>> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ResTask>> updateTask(@PathVariable Long id, @RequestBody ReqTask req) {
        return ResponseEntity.ok(taskService.updateTask(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.deleteTask(id));
    }

    @GetMapping
    public ResponseEntity<Page<ResTask>> getAllTasks(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(taskService.getAllTasks(page, size));
    }
}
