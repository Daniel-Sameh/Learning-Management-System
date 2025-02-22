package com.swe.lms.userManagement.controller;

import com.swe.lms.userManagement.repository.UserRepository;
import com.swe.lms.courseManagement.dto.CourseDTO;
import com.swe.lms.notification.entity.Notification;
import com.swe.lms.notification.service.NotificationService;
import com.swe.lms.security.dao.request.SignUpRequest;
import com.swe.lms.security.dao.request.SigninRequest;
import com.swe.lms.security.dao.response.JwtAuthenticationResponse;
import com.swe.lms.userManagement.Service.AuthenticationService;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.swe.lms.userManagement.Service.UserInfoService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authenticationService;

    private final NotificationService notificationService;
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> register(@RequestBody SignUpRequest request){
        return  ResponseEntity.ok(authenticationService.signup(request));
    }
    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> register(@RequestBody SigninRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("____________________________________:::::");
        System.out.println(authentication);
        System.out.println("____________________________________:::::");
        User user = authenticationService.getUserByUsername(request.getUsername());
        List<Notification> notifications = notificationService.getNotificationsByUser(user);
        JwtAuthenticationResponse response = authenticationService.signin(request);
        for (Notification notification : notifications){
            System.out.println("Notification: " + notification.getTitle());
            notification.setRead(true);
            notificationService.saveNotification(notification);
        }
        Map<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("token", response.getToken());
        if (!notifications.isEmpty()){
            System.out.println("NO IT IS NOT EMPTY THERE IS NOTIFICATIONS");
            List<Map<String, Object>> simplifiedNotifications = notifications.stream()
                    .map(notification -> {
                        Map<String, Object> notificationMap = new HashMap<>();
                        notificationMap.put("title", notification.getTitle());
                        notificationMap.put("message", notification.getMessage());
                        // Add other necessary fields
                        return notificationMap;
                    })
                    .collect(Collectors.toList());
//            response.setNotifications(notifications);
            finalResponse.put("notifications", simplifiedNotifications);
            System.out.println("After setting notifications");
        }else{
            finalResponse.put("notifications",null);
        }

        return ResponseEntity.ok(finalResponse);
    }


    @PutMapping("/update/profile/{userId}")
    public ResponseEntity< Map<String, Object>> updateprofile(@RequestBody Map<String, Object> payload, @RequestHeader("Authorization")String authorizationHeader, @PathVariable Long userId) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }
        String token = authorizationHeader.substring(7);

        User  updated = authenticationService.updateprofile(payload,token,userId);
        Map<String, Object> response = new HashMap<>();
        response.put("name", updated.getUsername());
        response.put("email", updated.getEmail());
        response.put("role", updated.getRole());
        return ResponseEntity.ok(response);
    }
    UserInfoService userInfoService;
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> viewUserProfile(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }

        String token = authorizationHeader.substring(7);

        User user = userInfoService.getUserFromToken(token)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }
//    @PostMapping("/register")
//    public ResponseEntity<String> CreateUser(@RequestBody UserDto user){
//        String response = String.valueOf(userService.CreateUser(user));
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//    @PostMapping("/generateToken")
//    public ResponseEntity<String> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
//        if (authentication.isAuthenticated()) {
//            String token = jwtService.generateToken(authRequest.getUsername());
//            return ResponseEntity.ok(token);
//        } else {
//            throw new UsernameNotFoundException("Invalid user request!");
//        }
//    }
//
//    @GetMapping("/user/{userid}")
//    public ResponseEntity<UserDto> GetUserById(@PathVariable("userid") long userid){
//        UserDto user = userService.GetUserById(userid);
//        return new ResponseEntity<>(user, HttpStatus.OK);
//    }
}
