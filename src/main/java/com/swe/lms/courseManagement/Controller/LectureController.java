package com.swe.lms.courseManagement.Controller;
import org.springframework.web.bind.annotation.*;
import com.swe.lms.courseManagement.Service.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {

    @Autowired
    private LectureService lectureService;

    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @PostMapping("/create")
    public ResponseEntity<String> createLecture(@RequestBody Map<String, Object> request, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        System.out.println("The token: " + token);
        String createdLecture = lectureService.createLecture(request, token);
        System.out.println("The response string: " + createdLecture);
        return ResponseEntity.ok(createdLecture);
    }

    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @PostMapping("/{lectureId}/start")
    public ResponseEntity<String> startLecture(@PathVariable Long lectureId) {
        String startedLecture = lectureService.startLecture(lectureId);
        return ResponseEntity.ok(startedLecture);
    }

    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @PostMapping("/{lectureId}/end")
    public ResponseEntity<String> endLecture(@PathVariable Long lectureId) {
       String lecture= lectureService.endLecture(lectureId);
        return ResponseEntity.ok(lecture);
    }
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping("/{lectureId}/attend")
    public ResponseEntity<String> attendLecture(@PathVariable Long lectureId,@RequestHeader("Authorization")String authorizationHeader,@RequestBody Map<String, Object> request) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }
        String otp = (String) request.get("otp");
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String attendanceResponse = lectureService.attend(lectureId, token,otp);
        if (attendanceResponse.equals("Invalid OTP")
                || attendanceResponse.equals("Lecture is not currently running")
                || attendanceResponse.equals("User is not enrolled in the course for this lecture")
                || attendanceResponse.equals("User has already attended the lecture")) {
            return ResponseEntity.badRequest().body(attendanceResponse);
        }
        return ResponseEntity.ok(attendanceResponse);
    }








//@PostMapping("/attend")
//public ResponseEntity<String> attendLecture(@RequestBody Map<String, Object> request) {
//    Long lectureId = Long.valueOf(request.get("lectureId").toString());
//    String otp = (String) request.get("otp");
//    Long userId = Long.valueOf(request.get("userId").toString());
//
//    String result = lectureService.attendLecture(lectureId, otp, userId);
//    return ResponseEntity.ok(result);
//}

}
