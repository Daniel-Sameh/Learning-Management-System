package com.swe.lms.notification.service;

import com.swe.lms.exception.ResourceNotFoundException;
import com.swe.lms.notification.entity.Notification;
import com.swe.lms.notification.repository.NotificationRepository;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private EmailNotificationService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotification(User receiver, String subject, String body) {
        Notification notification = Notification.builder()
                .title(subject)
                .message(body)
                .createdAt(LocalDateTime.now())
                .read(false).build();
        emailService.sendNotification(receiver.getEmail(), subject, body);
    }

    public void sendNotification(List<User> receivers, String subject, String body) {
        Notification notification = Notification.builder()
                .title(subject)
                .message(body)
                .createdAt(LocalDateTime.now())
                .read(false).build();
        notification.setUsers(new HashSet<>());
        System.out.println("Sending system notification to " + receivers.size() + " users");
        notification.getUsers().addAll(receivers);
        for (User receiver : receivers) {
            System.out.println("Sending system notification to " + receiver.getUsername());
//            notification.getUsers().add(receiver);
//            receiver.getNotifications().add(notification);

//            userRepository.save(receiver); //This might be a problem! (many to many, attribute,...)
            emailService.sendNotification(receiver.getEmail(), subject, body);
        }
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByUser(User user) {
        return notificationRepository.findByUsers_Id(user.getId());
    }
}
