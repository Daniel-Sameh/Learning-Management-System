package com.swe.lms.courseManagement.Service;

import com.swe.lms.courseManagement.Repository.LectureRepository;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.courseManagement.entity.Lecture;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class LectureService {
    private static final String SECRET_KEY = "9D0EB6B1C2E1FAD0F53A248F6C3B5E4E2F6D8G3H1I0J7K4L1M9N2O3P5Q0R7S9T1U4V2W6X0Y3Z";
    @Autowired
    private LectureRepository lectureRepository;

    public String startLecture(Long lectureId) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));

        String otp = String.format("%06d", new Random().nextInt(1000000));
    if(lecture.isRunning()==true){
        return "Lecture is already running";
    }
        lecture.setRunning(true);
        lecture.setOTP(otp);
        lectureRepository.save(lecture);
        return lecture.getOTP();
    }

    public String endLecture(Long lectureId) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        if(lecture.isRunning()==false){
            return "Lecture has already ended";
        }
        lecture.setRunning(false);
        lectureRepository.save(lecture);
        return "Lecture has ended";
    }
@Autowired
 private UserRepository userRepository;

 public String attend(Long lectureId, String token,String otp) {
        // Extract user ID from token
        String username;
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
            username = claims.getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token", e);
        }

        User user = userRepository.findByUsername(username)
             .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // Verify lecture
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));

        if (!lecture.isRunning()) {
            return "Lecture is not currently running";
        }
         if (!lecture.getOTP().equals(otp)) {
             return "Invalid OTP";
         }
         Course course = lecture.getCourse(); // Get the course of the lecture
         if (!course.getStudents().contains(user)) { // Check if the user is in the students list of the course
             return "User is not enrolled in the course for this lecture";
         }
         if (lecture.getAttendanceList().contains(user)) {
         return "User has already attended the lecture";
        }
         lecture.getAttendanceList().add(user);
         lectureRepository.save(lecture);

        return "User with ID " + user.getId() + " successfully attended the lecture with ID " + lectureId;
    }

//    private UserRepository userRepository;
//
//    public String attendLecture(Long lectureId, String otp, Long userId) {
//
//        Lecture lecture = lectureRepository.findById(lectureId)
//                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
//
//        if (!lecture.isRunning()) {
//            throw new IllegalStateException("Lecture is not running");
//        }
//
//        if (!lecture.getOTP().equals(otp)) {
//            throw new IllegalArgumentException("Invalid OTP");
//        }
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        Course course = lecture.getCourse();
//        if (!course.getStudents().contains(user)) {
//            throw new IllegalStateException("User is not enrolled in the course");
//        }
//
//        if (!lecture.getAttendanceList().contains(user)) {
//            lecture.getAttendanceList().add(user);
//            lectureRepository.save(lecture);
//            return "Attendance marked successfully";
//        } else {
//            return "User is already marked as attended";
//        }
//    }
}
