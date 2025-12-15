package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqGroupNotif;
import com.example.nazoratv2.dto.request.ReqTask;
import com.example.nazoratv2.dto.response.ResTask;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.entity.Task;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.TaskMapper;
import com.example.nazoratv2.repository.GroupRepository;
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
    private final GroupRepository groupRepository;
    private final TaskMapper taskMapper;
    private final NotificationService notificationService;

    public ApiResponse<String> createTask(ReqTask reqTask){
        User user = null;
        Group group = null;
        if (reqTask.getUserId() != null){
            user = userRepository.findById(reqTask.getUserId()).orElseThrow(
                    () -> new DataNotFoundException("User topilmadi")
            );
        } else if (reqTask.getGroupId() != null) {
            group = groupRepository.findById(reqTask.getGroupId()).orElseThrow(
                    () -> new DataNotFoundException("Group topilmadi")
            );
        }

        Task task = Task.builder()
                .title(reqTask.getTitle())
                .description(reqTask.getDescription())
                .deadline(reqTask.getDeadline())
                .group(group)
                .user(user)
                .build();
        taskRepository.save(task);

        if (group != null){
            notificationService.sendGroupNotification(ReqGroupNotif.builder()
                    .groupId(group.getId())
                    .title("Sfera Academy xabarnomasi")
                    .description("Sizga " + task.getTitle() + " nomli vazifa yuklandi! " +
                            "Siz " + task.getDeadline() + " sanagacha taskni tugatishingiz kerak")
                    .build());
        }
        return ApiResponse.success(null, "Task created");
    }



    public ApiResponse<String> updateTask(Long taskId, ReqTask reqTask){
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new DataNotFoundException("Task topilmadi")
        );

        User user = null;
        Group group = null;
        if (reqTask.getUserId() != null){
            user = userRepository.findById(reqTask.getUserId()).orElseThrow(
                    () -> new DataNotFoundException("User topilmadi")
            );
        } else if (reqTask.getGroupId() != null) {
            group = groupRepository.findById(reqTask.getGroupId()).orElseThrow(
                    () -> new DataNotFoundException("Group topilmadi")
            );
        }

        task.setTitle(reqTask.getTitle());
        task.setDescription(reqTask.getDescription());
        task.setDeadline(reqTask.getDeadline());
        task.setGroup(group);
        task.setUser(user);
        taskRepository.save(task);
        return ApiResponse.success(null, "Task updated");
    }


    public ApiResponse<String> deleteTask(Long taskId){
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new DataNotFoundException("Task topilmadi")
        );
        taskRepository.delete(task);
        return ApiResponse.success(null, "Task deleted");
    }


    public ApiResponse<ResTask> getTask(Long taskId){
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new DataNotFoundException("Task topilmadi")
        );

        return ApiResponse.success(taskMapper.toDto(task), "Task found");
    }


    public ApiResponse<List<ResTask>> getAllTasks(){
        List<ResTask> list = taskRepository.findAll().stream().map(taskMapper::toDto).toList();
        return ApiResponse.success(list, "Tasks found");
    }

}
