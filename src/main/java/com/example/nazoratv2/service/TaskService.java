package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqTask;
import com.example.nazoratv2.dto.response.ResTask;
import com.example.nazoratv2.entity.Task;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.mapper.TaskMapper;
import com.example.nazoratv2.repository.TaskRepository;
import com.example.nazoratv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Task createTask(Task task, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User topilmadi"));

        task.setId(null);
        task.setUser(user);
        return taskRepository.save(task);
    }


    public List<Task> getAllTasks(Long userId) {
        return taskRepository.findAllByUserId(userId);
    }


    public Task getOneTask(Long taskId, Long userId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task topilmadi"));
    }


    public Task updateTask(Long taskId, Long userId, Task newTask) {
        Task task = getOneTask(taskId, userId);

        task.setTitle(newTask.getTitle());
        task.setDescription(newTask.getDescription());

        return taskRepository.save(task);
    }

    // DELETE
    public void deleteTask(Long taskId, Long userId) {
        taskRepository.deleteByIdAndUserId(taskId, userId);
    }
}
