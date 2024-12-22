package com.swe.lms.courseManagement.Repository;

import com.swe.lms.courseManagement.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByCode(String code);

    Optional<Course> findById(Long courseId);
    Course findByNameOrCode(String name, String code);
    List<Course> findByStudentsId(Long studentId);

}

