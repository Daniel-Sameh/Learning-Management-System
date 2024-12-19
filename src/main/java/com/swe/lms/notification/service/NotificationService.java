package com.swe.lms.notification.service;

import com.swe.lms.exception.ResourceNotFoundException;
import com.swe.lms.notification.entity.Notification;
import com.swe.lms.notification.repository.NotificationRepository;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public void sendNotification(List<User> receivers, String subject, String body) {
        try {
            Notification notification = Notification.builder()
                    .title(subject)
                    .message(body)
                    .createdAt(LocalDateTime.now())
//                    .users(new HashSet<>())
                    .read(false).build();
            notification = notificationRepository.save(notification);
//        notification.setUsers(new HashSet<>());
            System.out.println("Sending system notification to " + receivers.size() + " users");
//        notification.getUsers().addAll(receivers); //THE PROBLEM IS HERE!!!
//        System.out.println("I will send to each user an email:...");
            for (User receiver : receivers) {
                User user = userRepository.findById(receiver.getId())
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // Add notification to user
                if (user.getNotifications() == null) {
                    user.setNotifications(new HashSet<>());
                }
                user.getNotifications().add(notification);

                // Save user
                userRepository.save(user);

//            userRepository.save(receiver); //This might be a problem! (many to many, attribute,...)
                System.out.println("Sending email notification to " + receiver.getEmail());
                emailService.sendNotification(receiver.getEmail(), subject, body);
            }
            notificationRepository.save(notification);
        }catch (Exception e){
            System.err.println("Error sending notification: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
//        userRepository.saveAll(receivers);
    }

    public List<Notification> getNotificationsByUser(User user) {
        return notificationRepository.findByUsers_Id(user.getId());
    }
}
