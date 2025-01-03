package com.swe.lms.courseManagement.Service;

import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.Repository.LectureRepository;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.courseManagement.entity.Lecture;
import com.swe.lms.exception.ResourceNotFoundException;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class LectureService {
    static Dotenv dotenv = Dotenv.load();

    private static final String SECRET_KEY = dotenv.get("SECRET_KEY");

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private CourseRepository courseRepository;

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
             .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // Verify lecture
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found"));

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
    public List<Lecture>  getLecturesByCourseId(Long courseId){

     List<Lecture> lecs=  lectureRepository.findByCourseId(courseId);
     return lecs;
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
    public String createLecture(Map<String, Object> request, String token){
        String username;
        String role;
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
            username = claims.getSubject();
            role = (String) claims.get("role");
            System.out.println("The role is: " + role);
        } catch (Exception e) {
            System.out.println("There is an error in the token");
            throw new IllegalArgumentException("Invalid token", e);
        }
        if (!role.equals("INSTRUCTOR")) {
            throw new IllegalArgumentException("Only instructors can create lectures");
        }
        Lecture lecture = new Lecture();
        for (Map.Entry<String, Object> entry : request.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        Course course = courseRepository.findById(Long.valueOf(request.get("courseId").toString()))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        lecture.setCourse((Course) course);

        String dateStr = (String) request.get("date");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter);
        lecture.setDate(dateTime);

        System.out.println("I set the course and date of the lecture");
        lecture.setName((String) request.get("name"));

        lecture.setRunning(false);

        System.out.println("I set all the attributes of the lecture");
        lectureRepository.save(lecture);
        return "Created lecture "+ request.get("name") +" with id " + lecture.getId() + " for the " + course.getName() + " course.";
    }

    public Map<String, Object> getLectureAttendanceStats(Long lectureId){
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found"));
        List<User> students = lecture.getAttendanceList();
        int totalStudents = lecture.getCourse().getStudents().size();
        int presentStudents = students.size();
        int absentStudents = totalStudents - presentStudents;
        float attendancePercentage = (float) presentStudents / totalStudents * 100;
        return Map.of(
                "totalStudents", totalStudents,
                "presentStudents", presentStudents,
                "absentStudents", absentStudents,
                "attendancePercentage", attendancePercentage
        );
    }
}
