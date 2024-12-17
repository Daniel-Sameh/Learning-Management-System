package com.swe.lms.courseManagement.Service;

import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.userManagement.Exception.ResourceNotFoundException;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

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
}
