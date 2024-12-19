package com.swe.lms.notification.controller;
import com.swe.lms.exception.ResourceNotFoundException;
import com.swe.lms.notification.dao.NotificationRequest;
import com.swe.lms.notification.service.EmailNotificationService;
import com.swe.lms.notification.service.NotificationService;
import com.swe.lms.notification.service.SystemNotificationService;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/email")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String sendEmailNotification(@RequestBody NotificationRequest request) {
        System.out.println("We are at the email notification controller");
        User user = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getReceiverId()));
        notificationService.sendNotification(user, request.getSubject(), request.getBody());
        return "Email Notification Sent Successfully!";
    }
}
