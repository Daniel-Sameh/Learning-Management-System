package com.swe.lms.courseManagement.Repository;

import com.swe.lms.courseManagement.entity.Lecture;
import com.swe.lms.courseManagement.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    List<Lecture> findByCourseId(Long courseId);
}