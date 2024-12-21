package com.swe.lms.courseManagement.Repository;

import com.swe.lms.courseManagement.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByCourseId(Long courseId);
}
