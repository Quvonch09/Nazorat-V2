package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqNotification;
import com.example.nazoratv2.dto.response.ResNotification;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.entity.Notification;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.NotificationMapper;
import com.example.nazoratv2.repository.NotificationRepository;
import com.example.nazoratv2.repository.StudentRepository;
import com.example.nazoratv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    public ApiResponse<String> saveNotification(ReqNotification req) {
        Student student = studentRepository.findById(req.getStudentId()).orElseThrow(() -> new DataNotFoundException("Student not found"));
        User parent = userRepository.findById(req.getParentId()).orElseThrow(() -> new DataNotFoundException("User not found"));

        Notification notification = Notification.builder()
                .message(req.getMessage())
                .description(req.getDescription())
                .student(student)
                .parent(parent)
                .isRead(req.isRead())
                .build();
        notificationRepository.save(notification);
        return ApiResponse.success(null,"success");
    }

    public ApiResponse<ResPageable> getNotifications(int page,int size) {
        PageRequest pageRequest = PageRequest.of(page,size);
        Page<Notification> notifications = notificationRepository.findAll(pageRequest);
        List<ResNotification> notificationsList = notifications.stream().map(notificationMapper::toNotificationDTO).toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(notifications.getTotalElements())
                .totalPage(notifications.getTotalPages())
                .body(notificationsList)
                .build();
        return ApiResponse.success(resPageable, "success");

    }

    public ApiResponse<ResNotification> getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Notification not found"));
        return ApiResponse.success(notificationMapper.toNotificationDTO(notification), "success");

    }

    public ApiResponse<String> deleteNotificationById(Long id) {
        Notification notif = notificationRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Notification not found"));
        notificationRepository.delete(notif);
        return ApiResponse.success(null,"success");
    }

}
