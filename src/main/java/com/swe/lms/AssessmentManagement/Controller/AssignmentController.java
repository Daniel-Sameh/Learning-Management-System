package com.swe.lms.AssessmentManagement.Controller;

import com.swe.lms.AssessmentManagement.Service.AssignmentService;
import com.swe.lms.AssessmentManagement.dto.AssignmentDto;
import com.swe.lms.AssessmentManagement.dto.AssignmentSubmissionDto;
import com.swe.lms.AssessmentManagement.entity.Assignment;
import com.swe.lms.AssessmentManagement.entity.AssignmentSubmission;
import com.swe.lms.userManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    @Autowired
    private AssignmentService assignmentService;

    @PostMapping("/create/course/{courseId}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<AssignmentDto> createAssignment(@PathVariable Long courseId,
                                                          @RequestParam(value="title") String title,
                                                          @RequestParam(value ="decription") String description,
                                                          @RequestParam(value = "deadline") String deadline,
                                                          @AuthenticationPrincipal User instructor) {
        System.out.println("Creating assignment...");
        System.out.println("Course ID: " + courseId);
        System.out.println("Title: " + title);
        System.out.println("Description: " + description);
        System.out.println("Deadline: " + deadline);
//        System.out.println("Instructor: " + instructor);
        Assignment assignment = assignmentService.createAssignment(courseId, title, description, deadline, instructor);
        return ResponseEntity.ok(new AssignmentDto(assignment.getTitle(), assignment.getDescription(), assignment.getDeadline().toString(), assignment.getCourse().getId(), instructor.getId()));
    }

    @PostMapping("/submit/{assignmentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<String> submitAssignment(@PathVariable Long assignmentId,
                                                   @RequestParam(value = "file") MultipartFile file,
                                                   @AuthenticationPrincipal User student) {
        String response = assignmentService.submitAssignment(assignmentId, file, student);
        if (response.equals("Assignment submission failed. Deadline has passed.")){
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok("Assignment submitted successfully.");
    }

    @GetMapping("/get/{assignmentId}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<AssignmentDto> getAssignment(@AuthenticationPrincipal User user, @PathVariable Long assignmentId) {
        Optional<Assignment> assignment = assignmentService.getAssignmentById(assignmentId);
        if (assignment.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        if (user.getRole().toString().equals("STUDENT") && !assignment.get().getCourse().getStudents().contains(user)) {
            return ResponseEntity.status(403).body(null);
        }else if (user.getRole().toString().equals("INSTRUCTOR") && !assignment.get().getInstructor().equals(user)) {
            return ResponseEntity.status(403).body(null);
        }
        return ResponseEntity.ok(new AssignmentDto(assignment.get().getTitle(), assignment.get().getDescription(), assignment.get().getDeadline().toString(), assignment.get().getCourse().getId(), assignment.get().getInstructor().getId()));
    }

    @GetMapping("/get/{assignmentId}/submissions")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> getSubmissions(@AuthenticationPrincipal User user, @PathVariable Long assignmentId) {
        Optional<Assignment> assignment = assignmentService.getAssignmentById(assignmentId);
        if (assignment.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        if (!assignment.get().getInstructor().equals(user)) {
            return ResponseEntity.status(403).body(null);
        }
        return ResponseEntity.ok(assignmentService.getSubmissions(assignmentId).stream().map(submission ->
                new AssignmentSubmissionDto(submission.getAssignment().getId(), submission.getStudent().getId(),
                        submission.getMedia(), submission.getSubmissionTime().toString(), submission.getGrade())));
    }

    @PostMapping("/grade/{assignmentId}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<String> gradeAssignment(@PathVariable Long assignmentId,
                                                  @RequestParam(value = "submissionId") Long submissionId,
                                                  @RequestParam(value = "grade") String grade,
                                                  @AuthenticationPrincipal User instructor) {
        Optional<Assignment> assignment = assignmentService.getAssignmentById(assignmentId);
        AssignmentSubmission assignmentSubmission = assignmentService.getSubmissionById(submissionId);
        if (assignment.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        if (!assignment.get().getInstructor().equals(instructor)) {
            return ResponseEntity.status(403).body(null);
        }
        if (assignmentSubmission.getAssignment().getId()!= assignmentId) {
            return ResponseEntity.status(400).body("Submission not found.");
        }
        String response = assignmentService.gradeAssignment(assignmentSubmission, Float.parseFloat(grade), instructor);
//        if (response.equals("Assignment not found.")) {
//            return ResponseEntity.status(404).body(response);
//        } else if (response.equals("Submission not found.")) {
//            return ResponseEntity.status(404).body(response);
//        } else if (response.equals("You are not the instructor of this assignment.")) {
//            return ResponseEntity.status(403).body(response);
//        }
        return ResponseEntity.ok(response);
    }

}
