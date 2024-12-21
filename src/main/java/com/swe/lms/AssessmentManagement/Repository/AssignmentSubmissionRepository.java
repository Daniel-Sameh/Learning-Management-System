package com.swe.lms.AssessmentManagement.Repository;

import com.swe.lms.AssessmentManagement.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findAllByAssignment_Id(Long assignmentId);
}
