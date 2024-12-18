package com.swe.lms.notification.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {
    @Autowired
    private JavaMailSender mailSender;
    public void sendNotification(String toEmail, String subject, String body) {
//        System.out.println(mailSender.toString());
//        SimpleMailMessage message = new SimpleMailMessage();
//
//        message.setTo(toEmail);
//        message.setSubject(subject);
//        message.setText(body);
//        message.setFrom("20221050@stud.fci-cu.edu.eg");
//        System.out.println(message.toString());
//        System.out.println("Sending email to " + toEmail);
//        System.out.println("Subject: " + subject);
//        System.out.println("Body: " + body);
//        mailSender.send(message);
//        System.out.println("Email sent successfully to " + toEmail);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("20221050@stud.fci-cu.edu.eg"); // Set the sender explicitly

            helper.setTo(toEmail); // Set the recipient email
            helper.setSubject(subject); // Set the subject
            String htmlContent =
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<meta charset='UTF-8'>" +
                            "<title>Email Notification</title>" +
                            "<style>" +
                            "body { font-family: 'Georgia', serif; margin: 0; padding: 0; background-color: #f0f8ff; display: flex; justify-content: center; align-items: center; height: 100vh; color: #333333; }" +
                            ".container { width: 80%; max-width: 600px; background-color: #ffffff; padding: 40px; border: 1px solid #dddddd; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); text-align: center; }" +
                            ".header { font-size: 32px; font-weight: bold; color: #007BFF; margin-bottom: 20px; }" +
                            ".content { font-size: 18px; line-height: 1.6; color: #333333; margin-bottom: 20px; background-color: #f9f9f9; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }" +
                            ".footer { font-size: 12px; color: #777777; margin-top: 20px; }" +
                            "</style>" +
                            "</head>" +
                            "<body>" +
                            "<div class='container'>" +
                            "<div class='header'>Learning Management System (LMS)</div>" +
                            "<div class='content'>" + body + "</div>" +
                            "<div class='footer'>This is an automated email. Please do not reply.<br>&copy; 2024 LMS Team</div>" +
                            "</div>" +
                            "</body>" +
                            "</html>";

            helper.setText(htmlContent, true);

            System.out.println("Sending email to " + toEmail);
            mailSender.send(mimeMessage);

            System.out.println("Email sent successfully to " + toEmail);
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
