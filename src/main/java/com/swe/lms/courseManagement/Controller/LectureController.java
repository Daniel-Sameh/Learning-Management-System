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

    @PostMapping("/{lectureId}/start")
    public ResponseEntity<String> startLecture(@PathVariable Long lectureId) {
        String startedLecture = lectureService.startLecture(lectureId);
        return ResponseEntity.ok(startedLecture);
    }

    @PostMapping("/{lectureId}/end")
    public ResponseEntity<String> endLecture(@PathVariable Long lectureId) {
       String lecture= lectureService.endLecture(lectureId);
        return ResponseEntity.ok(lecture);
    }
    @PostMapping("/{lectureId}/attend")
    public ResponseEntity<String> attendLecture(@PathVariable Long lectureId,@RequestHeader("Authorization")String authorizationHeader,@RequestBody Map<String, Object> request) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }
        String otp = (String) request.get("otp");
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String attendanceResponse = lectureService.attend(lectureId, token,otp);
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
