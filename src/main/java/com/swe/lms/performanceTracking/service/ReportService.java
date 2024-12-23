package com.swe.lms.performanceTracking.service;

import com.swe.lms.AssessmentManagement.Repository.QuizRepository;
import com.swe.lms.AssessmentManagement.dto.QuizDto;
import com.swe.lms.AssessmentManagement.entity.Assignment;
import com.swe.lms.AssessmentManagement.entity.AssignmentSubmission;
import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.AssessmentManagement.entity.QuizSubmission;
import com.swe.lms.courseManagement.dto.StudentDTO;
import com.swe.lms.courseManagement.entity.Lecture;
import com.swe.lms.exception.ResourceNotFoundException;
import com.swe.lms.userManagement.entity.User;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.Color.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReportService {
    @Autowired
    private  final QuizRepository quizRepository;

    public byte[] generatePerformanceReport(List<List<AssignmentSubmission>> assignments,
                                            List<Lecture> lectures,
                                            List<StudentDTO> students) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Create styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd hh:mm"));

            // Create Assignments sheet
            Sheet assignmentSheet = workbook.createSheet("Assignments");
            Row headerRow = assignmentSheet.createRow(0);
            String[] assignmentHeaders = {
                    "Student Name",
                    "Assignment Title",
                    "Deadline",
                    "Submission Date",
                    "Grade",
                    "Status",
                    "Course Name"
            };

            // Create assignment headers
            for (int i = 0; i < assignmentHeaders.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(assignmentHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (List<AssignmentSubmission> assignmentList : assignments) {
                for (AssignmentSubmission submission : assignmentList) {
                    Row row = assignmentSheet.createRow(rowNum++);

                    // Student Name
                    row.createCell(0).setCellValue(submission.getStudent().getUsername());

                    // Assignment Title
                    row.createCell(1).setCellValue(submission.getAssignment().getTitle());

                    // Deadline
                    Cell deadlineCell = row.createCell(2);
                    deadlineCell.setCellValue(submission.getAssignment().getDeadline().toString());
                    deadlineCell.setCellStyle(dateStyle);

                    // Submission Date
                    Cell submissionCell = row.createCell(3);
                    if (submission.getSubmissionTime() != null) {
                        submissionCell.setCellValue(submission.getSubmissionTime().toString());
                        submissionCell.setCellStyle(dateStyle);
                    }

                    // Grade
                    Cell gradeCell = row.createCell(4);
                    gradeCell.setCellValue(submission.getGrade());

                    // Status (Problem is HERE!!!!)
                    row.createCell(5).setCellValue(submission.getStatus() != null ?
                            submission.getStatus() : "Not Submitted");

                    // Course Name
                    row.createCell(6).setCellValue(submission.getAssignment().getCourse().getName());
                }
            }

            // Auto-size assignment columns
            for (int i = 0; i < assignmentHeaders.length; i++) {
                assignmentSheet.autoSizeColumn(i);
            }

            // Create Attendance sheet
            Sheet attendanceSheet = workbook.createSheet("Attendance");
            headerRow = attendanceSheet.createRow(0);
            String[] attendanceHeaders = {
                    "Student Name",
                    "Lecture Name",
                    "Date",
                    "Present",
                    "Course Name"
            };

            // Create attendance headers
            for (int i = 0; i < attendanceHeaders.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(attendanceHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            rowNum = 1;
            for (Lecture lecture : lectures) {
                for (StudentDTO student : students) {
                    Row row = attendanceSheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(student.getUsername());
                    row.createCell(1).setCellValue(lecture.getName());

                    Cell dateCell = row.createCell(2);
                    dateCell.setCellValue(lecture.getDate().toString());
                    dateCell.setCellStyle(dateStyle);

                    row.createCell(3).setCellValue(lecture.getAttendanceList().contains(student));
                    row.createCell(4).setCellValue(lecture.getCourse().getName());
                }
            }

            // Auto-size attendance columns
            for (int i = 0; i < attendanceHeaders.length; i++) {
                attendanceSheet.autoSizeColumn(i);
            }

            // Create Summary sheet
            Sheet summarySheet = workbook.createSheet("Summary");
            createSummarySheet(summarySheet, assignments, lectures, students, headerStyle);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void createSummarySheet(Sheet summarySheet,
                                    List<List<AssignmentSubmission>> assignments,
                                    List<Lecture> lectures,
                                    List<StudentDTO> students,
                                    CellStyle headerStyle) {
        Row headerRow = summarySheet.createRow(0);
        String[] headers = {
                "Student Name",
                "Average Grade",
                "Submission Rate",
                "Attendance Rate"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (StudentDTO student : students) {
            Row row = summarySheet.createRow(rowNum++);

            // Student Name
            row.createCell(0).setCellValue(student.getUsername());

            // Calculate average grade
            double avgGrade = calculateAverageGrade(assignments, student);
            row.createCell(1).setCellValue(String.format("%.2f", avgGrade));

            // Calculate submission rate
            double submissionRate = calculateSubmissionRate(assignments, student);
            row.createCell(2).setCellValue(String.format("%.2f%%", submissionRate));

            // Calculate attendance rate
            double attendanceRate = calculateAttendanceRate(lectures, student);
            row.createCell(3).setCellValue(String.format("%.2f%%", attendanceRate));
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            summarySheet.autoSizeColumn(i);
        }
    }

    private double calculateAverageGrade(List<List<AssignmentSubmission>> assignments, StudentDTO student) {
        return assignments.stream()
                .flatMap(List::stream)
                .filter(submission -> submission.getStudent().getId().equals(student.getId()))
                .mapToDouble(AssignmentSubmission::getGrade)
                .average()
                .orElse(0.0);
    }

    private double calculateSubmissionRate(List<List<AssignmentSubmission>> assignments, StudentDTO student) {
        long totalSubmissions = assignments.stream()
                .flatMap(List::stream)
                .filter(submission -> submission.getStudent().getId().equals(student.getId()))
                .filter(submission -> submission.getStatus() != null && submission.getStatus().equals("submitted"))
                .count();

        long totalAssignments = assignments.stream()
                .flatMap(List::stream)
                .filter(submission -> submission.getStudent().getId().equals(student.getId()))
                .count();

        return totalAssignments > 0 ? (double) totalSubmissions / totalAssignments * 100 : 0.0;
    }

    private double calculateAttendanceRate(List<Lecture> lectures, StudentDTO student) {
        long attendedLectures = lectures.stream()
                .filter(lecture -> lecture.getAttendanceList().contains(student))
                .count();

        return lectures.size() > 0 ? (double) attendedLectures / lectures.size() * 100 : 0.0;
    }
    public byte[] generatePerformanceCharts(List<AssignmentSubmission> assignments,
                                            List<Lecture> lectures,
                                            List<User> students) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 1. Grade Distribution Chart
            DefaultCategoryDataset gradeDataset = new DefaultCategoryDataset();
            Map<String, Integer> gradeRanges = new HashMap<>();
            gradeRanges.put("0-20", 0);
            gradeRanges.put("21-40", 0);
            gradeRanges.put("41-60", 0);
            gradeRanges.put("61-80", 0);
            gradeRanges.put("81-100", 0);

            for (AssignmentSubmission submission : assignments) {
                double grade = submission.getGrade();
                if (grade <= 20) gradeRanges.merge("0-20", 1, Integer::sum);
                else if (grade <= 40) gradeRanges.merge("21-40", 1, Integer::sum);
                else if (grade <= 60) gradeRanges.merge("41-60", 1, Integer::sum);
                else if (grade <= 80) gradeRanges.merge("61-80", 1, Integer::sum);
                else gradeRanges.merge("81-100", 1, Integer::sum);
            }

            gradeRanges.forEach((range, count) ->
                    gradeDataset.addValue(count, "Grades", range));

            JFreeChart gradeChart = ChartFactory.createBarChart(
                    "Grade Distribution",
                    "Grade Range",
                    "Number of Students",
                    gradeDataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            // 2. Attendance Chart
            DefaultPieDataset attendanceDataset = new DefaultPieDataset();
            double totalPossibleAttendance = lectures.size() * students.size();
            double totalActualAttendance = lectures.stream()
                    .mapToLong(lecture -> lecture.getAttendanceList().size())
                    .sum();

            double attendancePercentage = (totalActualAttendance / totalPossibleAttendance) * 100;
            double absencePercentage = 100 - attendancePercentage;

            attendanceDataset.setValue("Present", attendancePercentage);
            attendanceDataset.setValue("Absent", absencePercentage);

            JFreeChart attendanceChart = ChartFactory.createPieChart(
                    "Overall Attendance Rate",
                    attendanceDataset,
                    true,
                    true,
                    false
            );

            // 3. Assignment Comparison Chart
            DefaultCategoryDataset assignmentDataset = new DefaultCategoryDataset();
            Map<String, Double> assignmentAverages = assignments.stream()
                    .collect(Collectors.groupingBy(
                            submission -> submission.getAssignment().getTitle(),
                            Collectors.averagingDouble(AssignmentSubmission::getGrade)
                    ));

            assignmentAverages.forEach((assignment, average) ->
                    assignmentDataset.addValue(average, "Average Grade", assignment));

            JFreeChart assignmentChart = ChartFactory.createBarChart(
                    "Assignment Performance Comparison",
                    "Assignment",
                    "Average Grade",
                    assignmentDataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            // Save all charts to byte array
            ChartUtils.writeChartAsPNG(baos, gradeChart, 600, 400);
            ChartUtils.writeChartAsPNG(baos, attendanceChart, 600, 400);
            ChartUtils.writeChartAsPNG(baos, assignmentChart, 600, 400);

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate charts", e);
        }
    }

    public Map<String, Object> generatePerformanceStats(List<Map<String,Object>> courseStats) {

        if (courseStats == null || courseStats.isEmpty()) {
            throw new ResourceNotFoundException("Course stats is null or empty");
        }

        @SuppressWarnings("unchecked")
        List<List<AssignmentSubmission>> assignmentsList = (List<List<AssignmentSubmission>>) courseStats.get(0).get("assignments");
        @SuppressWarnings("unchecked")
        List<Lecture> lectures = (List<Lecture>) courseStats.get(1).get("lectures");
        @SuppressWarnings("unchecked")
        List<User> students= (List<User>) courseStats.get(2).get("students");
        @SuppressWarnings("unchecked")
        List<QuizDto> quizzes= (List<QuizDto>) courseStats.get(3).get("quizzes");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> quizStats = (List<Map<String,Object>>) courseStats.get(4).get("quizStats");
        ///////////////////////DEBUGGING:

        // Debug courseStats itself
        System.out.println("\n=== DEBUG: courseStats ===");
        System.out.println("courseStats size: " + courseStats.size());
//        for (int i = 0; i < courseStats.size(); i++) {
//            System.out.println("Map " + i + ": " + courseStats.get(i));
//            if (courseStats.get(i) != null) {
//                System.out.println("Map " + i + " type: " + courseStats.get(i).getClass().getName());
//                System.out.println("Map " + i + " keys: " + courseStats.get(i).keySet());
//            } else {
//                System.out.println("Map " + i + " is null");
//            }
//        }

// Debug assignments
        System.out.println("\n=== DEBUG: Assignments ===");
        Object assignmentsObj = courseStats.get(0).get("assignments");
        System.out.println("Assignments object type: " + (assignmentsObj != null ? assignmentsObj.getClass().getName() : "null"));
        if (assignmentsList != null) {
            System.out.println("AssignmentsList size: " + assignmentsList.size());
            for (int i = 0; i < assignmentsList.size(); i++) {
                List<AssignmentSubmission> submissions = assignmentsList.get(i);
                System.out.println("\nSubmissions list " + i + " size: " + submissions.size());
                for (AssignmentSubmission submission : submissions) {
                    System.out.println("Submission: " + submission.getStatus());
                    // Print specific fields of AssignmentSubmission
                    // Adjust these based on your AssignmentSubmission class fields
                    System.out.println("  - ID: " + submission.getId());
                    // Add other relevant fields
                }
            }
        } else {
            System.out.println("AssignmentsList is null");
        }

// Debug lectures
        System.out.println("\n=== DEBUG: Lectures ===");
        Object lecturesObj = courseStats.get(1).get("lectures");
        System.out.println("Lectures object type: " + (lecturesObj != null ? lecturesObj.getClass().getName() : "null"));
        if (lectures != null) {
            System.out.println("Lectures size: " + lectures.size());
            for (Lecture lecture : lectures) {
                System.out.println("Lecture: " + lecture.getName());
                // Print specific fields of Lecture
                System.out.println("  - ID: " + lecture.getId());
                // Add other relevant fields
            }
        } else {
            System.out.println("Lectures is null");
        }

// Debug students
        System.out.println("\n=== DEBUG: Students ===");
        Object studentsObj = courseStats.get(2).get("students");
        System.out.println("Students object type: " + (studentsObj != null ? studentsObj.getClass().getName() : "null"));
        if (students != null) {
            System.out.println("Students size: " + students.size());
            for (User student : students) {
                System.out.println("Student: " + student.getUsername());
                // Print specific fields of User
                System.out.println("  - ID: " + student.getId());
                System.out.println("  - Name: " + student.getUsername());
                // Add other relevant fields
            }
        } else {
            System.out.println("Students is null");
        }

// Debug quizzes
        System.out.println("\n=== DEBUG: Quizzes ===");
        Object quizzesObj = courseStats.get(3).get("quizzes");
        System.out.println("Quizzes object type: " + (quizzesObj != null ? quizzesObj.getClass().getName() : "null"));
        if (quizzes != null) {
            System.out.println("Quizzes size: " + quizzes.size());
            for (QuizDto quiz : quizzes) {
                System.out.println("Quiz: " + quiz.getTitle());
                // Print specific fields of QuizDto
                System.out.println("  - ID: " + quiz.getId());
                System.out.println("  - Title: " + quiz.getTitle());
                // Add other relevant fields
            }
        } else {
            System.out.println("Quizzes is null");
        }

// Debug quizStats
        System.out.println("\n=== DEBUG: Quiz Stats ===");
        Object quizStatsObj = courseStats.get(4).get("quizStats");
        System.out.println("QuizStats object type: " + (quizStatsObj != null ? quizStatsObj.getClass().getName() : "null"));
        if (quizStats != null) {
            System.out.println("QuizStats size: " + quizStats.size());
            for (int i = 0; i < quizStats.size(); i++) {
                Map<String, Object> stat = quizStats.get(i);
                System.out.println("\nQuiz Stat " + i + ":");
                if (stat != null) {
                    for (Map.Entry<String, Object> entry : stat.entrySet()) {
                        System.out.println("  - " + entry.getKey() + ": " + entry.getValue());
                        if (entry.getValue() != null) {
                            System.out.println("    Type: " + entry.getValue().getClass().getName());
                        }
                    }
                } else {
                    System.out.println("  Stat is null");
                }
            }
        } else {
            System.out.println("QuizStats is null");
        }

        ////////////////////////END_OF_DEBUGGING

//        Object item0 = courseStats.get(0);
//        Object item1 = courseStats.get(1);
//        Object item2 = courseStats.get(2);
//        Object item3 = courseStats.get(3);
//        Object item4 = courseStats.get(4);
//
//        List<List<AssignmentSubmission>> assignmentsList = item0 instanceof List ? (List<List<AssignmentSubmission>>) item0 : new ArrayList<>();
//        List<Lecture> lectures = item1 instanceof List ? (List<Lecture>) item1 : new ArrayList<>();
//        List<User> students = item2 instanceof List ? (List<User>) item2 : new ArrayList<>();
//        List<QuizDto> quizzes = item3 instanceof List ? (List<QuizDto>) item3 : new ArrayList<>();
//        List<Map<String,Object>> quizStats = item4 instanceof List ? (List<Map<String,Object>>) item4 : new ArrayList<>();

        System.out.println("We are in generate performance stats...");
        // Calculate assignment statistics per assignment and overall

        List<Map<String, Object>> assignmentsStats = new ArrayList<>();
        double overallAverageGrade = 0.0;
        long totalSubmissions = 0;
//        long onTimeSubmissions = 0;

        for (List<AssignmentSubmission> assignments : assignmentsList) {
            if (!assignments.isEmpty()) {
                // Get assignment details from first submission
                Assignment assignment = assignments.get(0).getAssignment();

                // Calculate statistics for this assignment
                double avgGrade = assignments.stream()
                        .mapToDouble(AssignmentSubmission::getGrade)
                        .average()
                        .orElse(0.0);
                System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::");
                System.out.println("HERE IS THE AVERAGE GRADE OF ASSIGNMENT: "+avgGrade);
                System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::");
                double highestGrade = assignments.stream()
                        .mapToDouble(AssignmentSubmission::getGrade)
                        .max()
                        .orElse(0.0);

                double lowestGrade = assignments.stream()
                        .mapToDouble(AssignmentSubmission::getGrade)
                        .min()
                        .orElse(0.0);

                long submittedCount = assignments.stream()
                        .filter(a -> a.getStatus() != null && a.getStatus().equals("submitted"))
                        .count();

//                long onTimeCount = assignments.stream()
//                        .filter(submission ->
//                                submission.getSubmissionTime() != null &&
//                                        submission.getAssignment().getDeadline() != null &&
//                                        submission.getSubmissionTime().isBefore(submission.getAssignment().getDeadline()))
//                        .count();

                Map<String, Object> assignmentStat = new HashMap<>();
                assignmentStat.put("assignmentId", assignment.getId());
                assignmentStat.put("assignmentTitle", assignment.getTitle());
                assignmentStat.put("deadline", assignment.getDeadline());
                assignmentStat.put("averageGrade", Math.round(avgGrade * 100.0) / 100.0);
                assignmentStat.put("highestGrade", highestGrade);
                assignmentStat.put("lowestGrade", lowestGrade);
                assignmentStat.put("submissionRate",
                        Math.round(((double) submittedCount / students.size()) * 100.0) / 100.0);
//                assignmentStat.put("onTimeSubmissionRate",
//                        Math.round(((double) onTimeCount / students.size()) * 100.0) / 100.0);
                assignmentStat.put("gradeDistribution", calculateGradeDistribution(assignments));

                assignmentsStats.add(assignmentStat);

                // Accumulate overall statistics
                overallAverageGrade += avgGrade * assignments.size();
                totalSubmissions += submittedCount;
//                onTimeSubmissions += onTimeCount;
            }
        }

        // Calculate overall statistics
        int totalAssignments = assignmentsList.stream()
                .mapToInt(List::size)
                .sum();

        double overallSubmissionRate = totalAssignments > 0 ?
                (double) totalSubmissions / (students.size() * assignmentsList.size()) * 100 : 0.0;

//        double overallOnTimeRate = totalAssignments > 0 ?
//                (double) onTimeSubmissions / (students.size() * assignmentsList.size()) * 100 : 0.0;

        overallAverageGrade = totalAssignments > 0 ?
                overallAverageGrade / totalAssignments : 0.0;

        // Calculate attendance statistics
        double attendanceRate = calculateAttendanceRate(lectures, students);

        // Create final statistics map
        Map<String, Object> stats = new HashMap<>();

        // General statistics
        stats.put("totalStudents", students.size());
        stats.put("totalAssignments", assignmentsList.size());
        stats.put("totalLectures", lectures.size());

        // Overall grade and submission statistics
        stats.put("overallAverageGrade", Math.round(overallAverageGrade * 100.0) / 100.0);
        stats.put("overallSubmissionRate", Math.round(overallSubmissionRate * 100.0) / 100.0);
//        stats.put("overallOnTimeSubmissionRate", Math.round(overallOnTimeRate * 100.0) / 100.0);

        // Per-assignment statistics
        stats.put("assignmentsStats", assignmentsStats);

        // Attendance statistics
        stats.put("attendanceRate", Math.round(attendanceRate * 100.0) / 100.0);
        stats.put("averageAttendancePerLecture", calculateAverageAttendancePerLecture(lectures));

        ///////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("before student statsss");
        Map<String,Object> studentsStats = new HashMap<>();
        for (User student : students) {
            List<AssignmentSubmission> studentAssignments = assignmentsList.stream()
                    .flatMap(List::stream)
                    .filter(submission -> submission.getStudent().getId().equals(student.getId()))
                    .collect(Collectors.toList());
            System.out.println("after studentAssignments");

            double grade = studentAssignments.stream()
                    .mapToDouble(AssignmentSubmission::getGrade)
                    .sum();
            System.out.println("grade 1");

            grade += lectures.stream()
                    .filter(lecture -> lecture.getAttendanceList().contains(student))
                    .count();
            System.out.println("grade 2");

            double quizzesScore = 0.0;
            for (Map<String, Object> quizSubmissionMap : quizStats) {
                System.out.println("We are in the quiz stats loooooooooppppppp.....");
                System.out.println("THE QUIZ SUBMISSION MAP: "+quizSubmissionMap);

                @SuppressWarnings("unchecked")
                Map<String, Object> submissions = (Map<String, Object>) quizSubmissionMap.get("submissions");
                System.out.println("THE SUBMISSIONS: "+submissions);
                System.out.println("THE STUDENT: "+student.getUsername());
                if (submissions != null) {
                    Object score = submissions.get(student.getUsername());
                    if (score != null) {
                        try {
                            // Handle different number types safely
                            if (score instanceof Number) {
                                quizzesScore += ((Number) score).doubleValue();
                            } else {
                                System.out.println("Warning: Score for student " + student.getUsername() +
                                        " is not a number: " + score);
                            }
                        } catch (Exception e) {
                            System.err.println("Error processing score for student " + student.getUsername() +
                                    ": " + e.getMessage());
                        }
                    } else {
                        System.out.println("No score found for student: " + student.getUsername());
                    }
                } else {
                    System.out.println("No submissions map found in quiz submission");
                }
            }
            grade+= quizzesScore;
            System.out.println("THE STUDENT: "+ student.getId()+", THE GRADE: "+grade);
            studentsStats.put(student.getUsername(), grade);
        }
        stats.put("studentStats", studentsStats);

        ////////////////////////////////////////////////////////////////////////////////////
        System.out.println("before quiz statsss");

        Map<String, Object> studentQuizStats = new HashMap<>();
        int passedCount=0;
        int failedCount=0;

        for(int i=0;i<quizzes.size();i++) {

            Map<String, Object> quizSubmissionMap = quizStats.get(i);
            @SuppressWarnings("unchecked")
            Map<String,Object> submissions= (Map<String,Object>)quizSubmissionMap.get("submissions");

            double fullmark = submissions.get("fullmark") instanceof Number ?
                    ((Number) submissions.get("fullmark")).doubleValue() : 0.0;
            double average = submissions.get("average") instanceof Number ?
                    ((Number) submissions.get("average")).doubleValue() : 0.0;

            Long quizId = (Long)submissions.get("quiz");
            Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
            submissions.remove("fullmark");
            submissions.remove("average");

            for (Map.Entry<String, Object> entry : submissions.entrySet()) {
                String username = entry.getKey();
                Object score = entry.getValue();
                System.out.println("Username: " + username + ", Score: " + score);
                double scorePercentage = (((Number) score).doubleValue() / fullmark) * 100;
                if (scorePercentage >= 50) {
                    passedCount++;
                } else {
                    failedCount++;
                }
            }

            int totalQuizSubmissions=passedCount+failedCount;
            float passPercentage= totalQuizSubmissions > 0 ? ((float) passedCount/totalQuizSubmissions) *100 : 0.0f;
            float failPercentage= totalQuizSubmissions > 0 ? ((float) failedCount/totalQuizSubmissions) *100 : 0.0f;
            List<Float> percentages=new ArrayList<>();
            percentages.add(passPercentage);
            percentages.add(failPercentage);
            studentQuizStats.put(quiz.getTitle(), percentages);
        }

        //////////////////////////////////////////////////////////////////

        stats.put("Quiz Submission Stats", studentQuizStats);
        System.out.println("Now we are returning the stat from service to controller...");
        return stats;
    }

    private Map<String, Long> calculateGradeDistribution(List<AssignmentSubmission> assignments) {
        return assignments.stream()
                .collect(Collectors.groupingBy(submission -> {
                    float grade = submission.getGrade();
                    if (grade >= 95) return "A+ (95-100)";
                    else if (grade >= 90) return "A (90-94)";
                    else if (grade >= 85) return "B+ (85-89)";
                    else if (grade >= 80) return "B (80-84)";
                    else if (grade >= 75) return "C+ (75-79)";
                    else if (grade >= 70) return "C (70-74)";
                    else if (grade >= 65) return "D+ (65-69)";
                    else if (grade >= 60) return "D (60-64)";
                    else return "F (Below 60)";
                }, Collectors.counting()));
    }

    private double calculateAttendanceRate(List<Lecture> lectures, List<User> students) {
        if (lectures.isEmpty() || students.isEmpty()) return 0.0;

        int totalPossibleAttendances = lectures.size() * students.size();
        int actualAttendances = lectures.stream()
                .mapToInt(lecture -> lecture.getAttendanceList().size())
                .sum();

        return (double) actualAttendances / totalPossibleAttendances * 100;
    }

    private double calculateAverageAttendancePerLecture(List<Lecture> lectures) {
        if (lectures.isEmpty()) return 0.0;

        return lectures.stream()
                .mapToInt(lecture -> lecture.getAttendanceList().size())
                .average()
                .orElse(0.0);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    public byte[] generateCharts(Map<String, Object> statistics) throws IOException {
        try {
            System.out.println("Starting chart generation...");

            // Create a new document with multiple charts
            BufferedImage combined = new BufferedImage(1600, 1200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = combined.createGraphics();
            java.awt.Color WHITE = new java.awt.Color(255, 255, 255);
            g2d.setColor(WHITE);
            g2d.fillRect(0, 0, 1600, 1200);

//            System.out.println("Creating submission rate chart...");
//            JFreeChart submissionChart = createSubmissionRateChart(statistics);
//            if (submissionChart == null) {
//                throw new IllegalStateException("Failed to create submission rate chart");
//            }

            System.out.println("Creating grade distribution chart...");
            JFreeChart gradeChart = createGradeDistributionChart(statistics);
            if (gradeChart == null) {
                throw new IllegalStateException("Failed to create grade distribution chart");
            }

            System.out.println("Creating attendance chart...");
            JFreeChart attendanceChart = createAttendanceChart(statistics);
            if (attendanceChart == null) {
                throw new IllegalStateException("Failed to create attendance chart");
            }

            System.out.println("Creating assignment performance chart...");
            JFreeChart assignmentPerformanceChart = createAssignmentPerformanceChart(statistics);
            if (assignmentPerformanceChart == null) {
                throw new IllegalStateException("Failed to create assignment performance chart");
            }

            System.out.println("Creating Quiz Performance Chart");
            JFreeChart quizPerformanceChart = createQuizPerformanceChart(statistics);
            if (quizPerformanceChart == null) {
                throw new IllegalStateException("Failed to create quiz performance chart");
            }

            System.out.println("Drawing charts...");
            try {
                gradeChart.draw(g2d, new Rectangle2D.Double(0, 0, 800, 600));
                quizPerformanceChart.draw(g2d, new Rectangle2D.Double(800, 0, 800, 600));
                attendanceChart.draw(g2d, new Rectangle2D.Double(0, 600, 800, 600));
                assignmentPerformanceChart.draw(g2d, new Rectangle2D.Double(800, 600, 800, 600));
            } catch (Exception e) {
                throw new IOException("Error drawing charts: " + e.getMessage(), e);
            }

            g2d.dispose();

            System.out.println("Converting to byte array...");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            boolean success = ImageIO.write(combined, "png", baos);
            if (!success) {
                throw new IOException("Failed to write image to byte array");
            }

            System.out.println("Chart generation completed successfully");
            return baos.toByteArray();

        } catch (Exception e) {
            System.err.println("Error generating charts: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to generate charts: " + e.getMessage(), e);
        }
    }
    private JFreeChart createQuizPerformanceChart(Map<String, Object> statistics) {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            Map<String, Object> quizStats = (Map<String, Object>) statistics.get("Quiz Submission Stats");

            if (quizStats == null || quizStats.isEmpty()) {
                dataset.addValue(0, "Quizzes", "No Data");
                return ChartFactory.createBarChart(
                        "Quiz Performance (No Data)",
                        "Quiz",
                        "Percentage",
                        dataset,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false
                );
            }

            // Add data for each quiz
            for (Map.Entry<String, Object> entry : quizStats.entrySet()) {
                String quizTitle = entry.getKey();
                @SuppressWarnings("unchecked")
                List<Float> percentages = (List<Float>) entry.getValue();

                // Add passing percentage (index 0)
                dataset.addValue(percentages.get(0), "Passing", quizTitle);
                // Add failing percentage (index 1)
                dataset.addValue(percentages.get(1), "Failing", quizTitle);
            }

            // Create the chart
            JFreeChart chart = ChartFactory.createStackedBarChart(
                    "Quiz Performance",
                    "Quiz",
                    "Percentage",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,  // include legend
                    true,  // include tooltips
                    false  // include URLs
            );

            // Customize the appearance
            CategoryPlot plot = (CategoryPlot) chart.getPlot();

            // Set colors for passing (green) and failing (red)
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, Color.GREEN);  // Green for passing
            renderer.setSeriesPaint(1, Color.RED);  // Red for failing

            // Customize the range axis to show percentages from 0 to 100
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setRange(0.0, 100.0);
            rangeAxis.setTickUnit(new NumberTickUnit(10));

            // Add value labels on the bars
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());

            return chart;

        } catch (Exception e) {
            System.err.println("Error creating quiz performance chart: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private JFreeChart createSubmissionRateChart(Map<String, Object> statistics) {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            Double submissionRate = convertToDouble(statistics.get("overallSubmissionRate"));
//            Double onTimeRate = convertToDouble(statistics.get("overallOnTimeSubmissionRate"));

            dataset.addValue(submissionRate, "Submission Rate", "Overall");
//            dataset.addValue(onTimeRate, "On-Time Rate", "Overall");

            return ChartFactory.createBarChart(
                    "Submission Rates",
                    "Type",
                    "Percentage",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );
        } catch (Exception e) {
            System.err.println("Error creating submission rate chart: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to safely convert objects to Double
    private Double convertToDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private JFreeChart createGradeDistributionChart(Map<String, Object> statistics) {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//            List<Map<String, Object>> assignmentsStats = (List<Map<String, Object>>) statistics.get("assignmentsStats");
            @SuppressWarnings("unchecked")
            Map<String, Object> studentStats = (Map<String, Object>) statistics.get("studentStats");
            System.out.println("IN GRADE DISTRIBUTION CHART");
            for (Map.Entry<String, Object> entry : studentStats.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }
            System.out.println("----------------------------------------");
            if (studentStats.isEmpty()) {
                dataset.addValue(0, "Grades", "No Data");
                return ChartFactory.createLineChart(
                        "Grade Distribution (No Data)",
                        "Grade Range",
                        "Frequency",
                        dataset,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false
                );
            }


            Map<String, Long> totalGradeDistribution = new LinkedHashMap<>();  // Changed to Long
            List<String> gradeCategories = Arrays.asList(
                    "F (Below 60)", "D (60-64)", "D+ (65-69)", "C (70-74)", "C+ (75-79)",
                    "B (80-84)", "B+ (85-89)", "A (90-94)", "A+ (95-100)"
            );

            for (String category : gradeCategories) {
                totalGradeDistribution.put(category, 0L);
            }

            for (Object gradeObj : studentStats.values()) {
                double grade = convertToDouble(gradeObj);
                String gradeCategory;
                if (grade >= 95) {
                    gradeCategory = "A+ (95-100)";
                } else if (grade >= 90) {
                    gradeCategory = "A (90-94)";
                } else if (grade >= 85) {
                    gradeCategory = "B+ (85-89)";
                } else if (grade >= 80) {
                    gradeCategory = "B (80-84)";
                } else if (grade >= 75) {
                    gradeCategory = "C+ (75-79)";
                } else if (grade >= 70) {
                    gradeCategory = "C (70-74)";
                } else if (grade >= 65) {
                    gradeCategory = "D+ (65-69)";
                } else if (grade >= 60) {
                    gradeCategory = "D (60-64)";
                } else {
                    gradeCategory = "F (Below 60)";
                }
                totalGradeDistribution.merge(gradeCategory, 1L, Long::sum);
            }
            for (String category : gradeCategories) {
                if (totalGradeDistribution.get(category) == 0) {
                    totalGradeDistribution.remove(category);
                }
            }
//            for (Map<String, Object> assignment : assignmentsStats) {
//                @SuppressWarnings("unchecked")
//                Map<String, Object> distribution = (Map<String, Object>) assignment.get("gradeDistribution");  // Changed to Object
//                if (distribution != null) {
//                    distribution.forEach((grade, count) -> {
//                        Long longCount;
//                        if (count instanceof Integer) {
//                            longCount = ((Integer) count).longValue();
//                        } else if (count instanceof Long) {
//                            longCount = (Long) count;
//                        } else {
//                            longCount = Long.valueOf(count.toString());
//                        }
//                        totalGradeDistribution.merge(grade, longCount, Long::sum);
//                    });
//                }
//            }

//            if (totalGradeDistribution.isEmpty()) {
//                dataset.setValue("No Grades", 1);
//            } else {
//                totalGradeDistribution.forEach((grade, count) ->
//                        dataset.setValue(grade, count.doubleValue())  // Convert to double for the dataset
//                );
//            }
//
//            return ChartFactory.createPieChart(
//                    "Grade Distribution",
//                    dataset,
//                    true,
//                    true,
//                    false
//            );
            if (totalGradeDistribution.isEmpty()) {
                dataset.addValue(0, "Grades", "No Grades");
            } else {
                totalGradeDistribution.forEach((grade, count) ->
                        dataset.addValue(count, "Grades", grade)
                );
            }

            return ChartFactory.createLineChart(
                    "Grade Distribution",
                    "Grade Range",
                    "Frequency",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );
        } catch (Exception e) {
            System.err.println("Error creating grade distribution chart: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

// Update other chart creation methods similarly...

//    private JFreeChart createSubmissionRateChart(Map<String, Object> statistics) {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        dataset.addValue((Number) statistics.get("overallSubmissionRate"), "Submission Rate", "Overall");
//        dataset.addValue((Number) statistics.get("overallOnTimeSubmissionRate"), "On-Time Rate", "Overall");
//
//        return ChartFactory.createBarChart(
//                "Submission Rates",
//                "Type",
//                "Percentage",
//                dataset,
//                PlotOrientation.VERTICAL,
//                true,
//                true,
//                false
//        );
//    }

//    private JFreeChart createGradeDistributionChart(Map<String, Object> statistics) {
//        DefaultPieDataset dataset = new DefaultPieDataset();
//        @SuppressWarnings("unchecked")
//        List<Map<String, Object>> assignmentsStats = (List<Map<String, Object>>) statistics.get("assignmentsStats");
//
//        // Aggregate grade distributions across all assignments
//        Map<String, Integer> totalGradeDistribution = new HashMap<>();
//        for (Map<String, Object> assignment : assignmentsStats) {
//            @SuppressWarnings("unchecked")
//            Map<String, Integer> distribution = (Map<String, Integer>) assignment.get("gradeDistribution");
//            distribution.forEach((grade, count) ->
//                    totalGradeDistribution.merge(grade, count, Integer::sum));
//        }
//
//        totalGradeDistribution.forEach(dataset::setValue);
//
//        return ChartFactory.createPieChart(
//                "Grade Distribution",
//                dataset,
//                true,
//                true,
//                false
//        );
//    }

    private JFreeChart createAttendanceChart(Map<String, Object> statistics) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        System.out.println("WE ARE IN ATTENDANCE CHART");
        System.out.println(statistics.get("attendanceRate"));
        System.out.println(statistics.get("averageAttendancePerLecture"));
        System.out.println("--------------------------------------");
        dataset.addValue((Number) statistics.get("attendanceRate"), "Attendance", "Overall");
        dataset.addValue((Number) statistics.get("averageAttendancePerLecture"), "Average per Lecture", "Overall");

        return ChartFactory.createBarChart(
                "Attendance Statistics",
                "Type",
                "Percentage",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

//    private JFreeChart createAssignmentPerformanceChart(Map<String, Object> statistics) {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        @SuppressWarnings("unchecked")
//        List<Map<String, Object>> assignmentsStats = (List<Map<String, Object>>) statistics.get("assignmentsStats");
//        System.out.println("WE ARE IN ASSIGNMENT PERFORMANCE CHART");
//        for (Map<String, Object> assignment : assignmentsStats) {
//            String title = (String) assignment.get("assignmentTitle");
//            System.out.println(title);
//            dataset.addValue((Number) assignment.get("averageGrade"), "Average Grade", title);
//            dataset.addValue((Number) assignment.get("highestGrade"), "Highest Grade", title);
//            dataset.addValue((Number) assignment.get("lowestGrade"), "Lowest Grade", title);
//        }
//        System.out.println("-----------------------------------------");
//
//        return ChartFactory.createLineChart(
//                "Assignment Performance",
//                "Assignment",
//                "Grade",
//                dataset,
//                PlotOrientation.VERTICAL,
//                true,
//                true,
//                false
//        );
//    }
    private JFreeChart createAssignmentPerformanceChart(Map<String, Object> statistics) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Safely extract assignmentsStats
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> assignmentsStats = (List<Map<String, Object>>) statistics.get("assignmentsStats");

        if (assignmentsStats == null || assignmentsStats.isEmpty()) {
            System.out.println("No assignment statistics available to display.");
            // Optionally, populate with placeholder data to avoid an empty graph
            dataset.addValue(0, "Average Grade", "No Data");
            dataset.addValue(0, "Highest Grade", "No Data");
            dataset.addValue(0, "Lowest Grade", "No Data");
        } else {
            for (Map<String, Object> assignment : assignmentsStats) {
                if (assignment == null) {
                    System.out.println("Skipping null assignment entry.");
                    continue;
                }

                String title = (String) assignment.get("assignmentTitle");
                System.out.println("ASSIGNMENT TITLE: "+title);
                System.out.println("ASSIGNMENT AVERAGE GRADE: "+assignment.get("averageGrade"));
                System.out.println("ASSIGNMENT HIGHEST GRADE: "+assignment.get("highestGrade"));
                System.out.println("ASSIGNMENT LOWEST GRADE: "+assignment.get("lowestGrade"));
                System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                Number averageGrade = (Number) assignment.getOrDefault("averageGrade", 0);
                Number highestGrade = (Number) assignment.getOrDefault("highestGrade", 0);
                Number lowestGrade = (Number) assignment.getOrDefault("lowestGrade", 0);

                if (title == null || averageGrade == null || highestGrade == null || lowestGrade == null) {
                    System.out.println("Invalid data for assignment: " + assignment);
                    continue;
                }

                dataset.addValue(averageGrade, "Average Grade", title);
                dataset.addValue(highestGrade, "Highest Grade", title);
                dataset.addValue(lowestGrade, "Lowest Grade", title);
            }
        }

        return ChartFactory.createBarChart(
                "Assignment Performance",
                "Assignment",
                "Grade",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

    }
//    private JFreeChart createAssignmentPerformanceChart(Map<String, Object> statistics) {
//        DefaultPieDataset pieDataset = new DefaultPieDataset();
//
//        @SuppressWarnings("unchecked")
//        List<Map<String, Object>> assignmentsStats = (List<Map<String, Object>>) statistics.get("assignmentsStats");
//
//        if (assignmentsStats == null || assignmentsStats.isEmpty()) {
//            System.out.println("No assignment statistics available to display.");
//            pieDataset.setValue("No Data", 1); // Placeholder for no data
//        } else {
//            for (Map<String, Object> assignment : assignmentsStats) {
//                if (assignment == null) {
//                    System.out.println("Skipping null assignment entry.");
//                    continue;
//                }
//
//                String title = (String) assignment.get("assignmentTitle");
//                Number averageGrade = (Number) assignment.getOrDefault("averageGrade", 0);
//
//                if (title == null || averageGrade == null) {
//                    System.out.println("Invalid data for assignment: " + assignment);
//                    continue;
//                }
//
//                // Add data for the pie chart (using average grades as an example metric)
//                pieDataset.setValue(title, averageGrade.doubleValue());
//            }
//        }
//
//        return ChartFactory.createPieChart(
//                "Average Assignments Grades Distribution", // Chart title
//                pieDataset,                   // Data
//                true,                         // Include legend
//                true,                         // Use tooltips
//                false                         // URLs
//        );
//    }


}