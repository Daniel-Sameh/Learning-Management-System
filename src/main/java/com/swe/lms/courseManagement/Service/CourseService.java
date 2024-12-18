package com.swe.lms.courseManagement.Service;

import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.notification.service.EmailNotificationService;
import com.swe.lms.userManagement.Exception.ResourceNotFoundException;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailNotificationService emailService;

    public void enrollUserInCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!course.getStudents().contains(user)) {
            course.getStudents().add(user);
            courseRepository.save(course);
        } else {
            throw new IllegalStateException("User is already enrolled in this course");
        }
    }


    public Course createCourse(Course course) {

        if (courseRepository.existsByCode(course.getCode())) {
            throw new IllegalArgumentException("Course code already exists");
        }
        return courseRepository.save(course);
    }

    public void notify(String subject, String body, Course course){
        List<User> students = course.getStudents();
        for(User student: students){
            // Send notification to each student
            emailService.sendNotification(student.getEmail(), subject, body);
        }
    }
    public Course updateCourse(Long courseId, Course updatedCourseDetails) {
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        existingCourse.setName(updatedCourseDetails.getName());
        existingCourse.setCode(updatedCourseDetails.getCode());

        if (updatedCourseDetails.getInstructor() != null) {
            User instructor = userRepository.findById(updatedCourseDetails.getInstructor().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
            existingCourse.setInstructor(instructor);
        }

        return courseRepository.save(existingCourse);
    }


    public Optional<Course> findById(Long courseId) {
        return courseRepository.findById(courseId);
    }

}
