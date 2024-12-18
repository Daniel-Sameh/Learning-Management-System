package com.swe.lms.notification.controller;
import com.swe.lms.notification.dao.NotificationRequest;
import com.swe.lms.notification.service.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private EmailNotificationService emailService;

    @PostMapping("/email")
    public String sendEmailNotification(@RequestBody NotificationRequest request) {
        System.out.println("We are at the email notification controller");
        emailService.sendNotification(request.getToEmail(), request.getSubject(), request.getBody());
        return "Email Notification Sent Successfully!";
    }
}
