package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqTask;
import com.example.nazoratv2.dto.response.ResTask;
import com.example.nazoratv2.entity.Task;
import com.example.nazoratv2.mapper.TaskMapper;
import com.example.nazoratv2.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;


    public ApiResponse<ResTask> createTask(ReqTask req) {
        Task task = taskMapper.toEntity(req);
        taskRepository.save(task);
        return new ApiResponse<>("Task created successfully", true, taskMapper.toDto(task));
    }


    public ApiResponse<ResTask> getTaskById(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) return new ApiResponse<>("Task not found", false);
        return new ApiResponse<>("Success", true, taskMapper.toDto(optionalTask.get()));
    }


    public ApiResponse<ResTask> updateTask(Long id, ReqTask req) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) return new ApiResponse<>("Task not found", false);

        Task task = optionalTask.get();
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setDeadline(String.valueOf(req.getDeadline()));
        taskRepository.save(task);

        return new ApiResponse<>("Task updated successfully", true, taskMapper.toDto(task));
    }


    public ApiResponse<Void> deleteTask(Long id) {
        if (!taskRepository.existsById(id)) return new ApiResponse<>("Task not found", false);
        taskRepository.deleteById(id);
        return new ApiResponse<>("Task deleted successfully", true);
    }

    public Page<ResTask> getAllTasks(int page, int size) {
        return taskRepository.findAll(PageRequest.of(page, size))
                .map(taskMapper::toDto);
    }
}
