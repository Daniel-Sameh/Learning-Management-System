package com.swe.lms.notification.controller;

import com.swe.lms.notification.dao.NotificationRequest;
import com.swe.lms.security.dao.request.SigninRequest;
import com.swe.lms.userManagement.entity.Role;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class NotificationControllerTest {

    @LocalServerPort
    private int port;

    private String baseUrl;
    private String apiUrl;
    private String jwtToken;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() {
        baseUrl = String.format("http://localhost:%d/api/notifications", port);
        apiUrl = String.format("http://localhost:%d/api", port);


        JavaMailSenderImpl mockMailSender = (JavaMailSenderImpl) javaMailSender;
        doNothing().when(mockMailSender).send(any(jakarta.mail.internet.MimeMessage.class));

        createAdminUserAndSignIn();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RestTemplate restTemplate(RestTemplateBuilder builder) {
            return builder.build();
        }

        @Bean
        @Primary
        public JavaMailSender javaMailSender() {
            JavaMailSenderImpl mailSender = Mockito.spy(new JavaMailSenderImpl());
            mailSender.setHost("localhost");
            mailSender.setPort(3025);
            mailSender.setUsername("test@localhost");
            mailSender.setPassword("test");

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "false");
            props.put("mail.smtp.starttls.enable", "false");
            props.put("mail.debug", "true");

            return mailSender;
        }
    }

    private void createAdminUserAndSignIn() {
        final String rawPassword = "adminPassword";

        try {
            if (userRepository.findByUsername("adminUser").isEmpty()) {
                User adminUser = new User();
                adminUser.setUsername("adminUser");
                adminUser.setPassword(passwordEncoder.encode(rawPassword));
                adminUser.setEmail("admin@example.com");
                adminUser.setRole(Role.ADMIN);
                userRepository.save(adminUser);
            }

            SigninRequest signinRequest = new SigninRequest();
            signinRequest.setUsername("adminUser");
            signinRequest.setPassword(rawPassword);

            HttpHeaders signinHeaders = new HttpHeaders();
            signinHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SigninRequest> signinEntity = new HttpEntity<>(signinRequest, signinHeaders);

            ResponseEntity<Map<String, Object>> signinResponse = restTemplate.exchange(
                    apiUrl + "/signin",
                    HttpMethod.POST,
                    signinEntity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> responseBody = signinResponse.getBody();
            if (responseBody != null && responseBody.containsKey("token")) {
                this.jwtToken = (String) responseBody.get("token");
                restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
                    request.getHeaders().set("Authorization", "Bearer " + jwtToken);
                    return execution.execute(request, body);
                }));
            } else {
                throw new RuntimeException("Failed to obtain JWT token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Setup failed", e);
        }
    }

    @Test
    void testSendEmailNotification() {

        User receiver = new User();
        receiver.setUsername("testReceiver");
        receiver.setEmail("receiver@test.com");
        receiver.setRole(Role.STUDENT);
        receiver = userRepository.save(receiver);


        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setReceiverId(receiver.getId());
        notificationRequest.setSubject("Test Notification");
        notificationRequest.setBody("This is a test notification message");


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NotificationRequest> requestEntity = new HttpEntity<>(notificationRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/email",
                HttpMethod.POST,
                requestEntity,
                String.class
        );


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email Notification Sent Successfully!", response.getBody());


        Mockito.verify((JavaMailSenderImpl)javaMailSender, Mockito.times(1))
                .send(any(jakarta.mail.internet.MimeMessage.class));
    }
}