package com.swe.lms.notification.service;

import com.swe.lms.notification.entity.Notification;
import com.swe.lms.notification.repository.NotificationRepository;
import com.swe.lms.userManagement.entity.User;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SystemNotificationService {
    @Autowired
    private static NotificationRepository notificationRepository;
    public void sendNotification(User receiver, String subject, String body) {
        Notification notification = Notification.builder()
                .title(subject)
                .message(body)
                .createdAt(LocalDateTime.now())
                .read(false).build();
        notificationRepository.save(notification);
    }

}
