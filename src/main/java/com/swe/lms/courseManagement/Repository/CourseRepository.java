package com.swe.lms.courseManagement.Repository;

import com.swe.lms.courseManagement.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByCode(String code);
}