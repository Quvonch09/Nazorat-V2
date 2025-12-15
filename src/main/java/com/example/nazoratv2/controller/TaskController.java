package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqTask;
import com.example.nazoratv2.dto.response.ResTask;
import com.example.nazoratv2.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;


   @PostMapping
    public ResponseEntity<ApiResponse<String>> saveTask(@RequestBody ReqTask reqTask){
       return ResponseEntity.ok(taskService.createTask(reqTask));
   }


   @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<String>> updateTask(@PathVariable Long taskId, @RequestBody ReqTask reqTask){
       return ResponseEntity.ok(taskService.updateTask(taskId, reqTask));
   }


   @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<String>> deleteTask(@PathVariable Long taskId){
       return ResponseEntity.ok(taskService.deleteTask(taskId));
   }


   @GetMapping
    public ResponseEntity<ApiResponse<List<ResTask>>> getAllTasks(){
       return ResponseEntity.ok(taskService.getAllTasks());
   }


   @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<ResTask>> getTask(@PathVariable Long taskId){
       return ResponseEntity.ok(taskService.getTask(taskId));
   }


}
