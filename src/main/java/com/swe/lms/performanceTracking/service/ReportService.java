package com.swe.lms.performanceTracking.service;

import com.swe.lms.AssessmentManagement.entity.Assignment;
import com.swe.lms.AssessmentManagement.entity.AssignmentSubmission;
import com.swe.lms.courseManagement.dto.StudentDTO;
import com.swe.lms.courseManagement.entity.Lecture;
import com.swe.lms.userManagement.entity.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

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

                    // Status
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

    public Map<String, Object> generatePerformanceStats(List<List<AssignmentSubmission>> assignmentsList,
                                                        List<Lecture> lectures,
                                                        List<User> students) {
        // Calculate assignment statistics per assignment and overall
        List<Map<String, Object>> assignmentsStats = new ArrayList<>();
        double overallAverageGrade = 0.0;
        long totalSubmissions = 0;
        long onTimeSubmissions = 0;

        for (List<AssignmentSubmission> assignments : assignmentsList) {
            if (!assignments.isEmpty()) {
                // Get assignment details from first submission
                Assignment assignment = assignments.get(0).getAssignment();

                // Calculate statistics for this assignment
                double avgGrade = assignments.stream()
                        .mapToDouble(AssignmentSubmission::getGrade)
                        .average()
                        .orElse(0.0);

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

                long onTimeCount = assignments.stream()
                        .filter(submission ->
                                submission.getSubmissionTime() != null &&
                                        submission.getAssignment().getDeadline() != null &&
                                        submission.getSubmissionTime().isBefore(submission.getAssignment().getDeadline()))
                        .count();

                Map<String, Object> assignmentStat = new HashMap<>();
                assignmentStat.put("assignmentId", assignment.getId());
                assignmentStat.put("assignmentTitle", assignment.getTitle());
                assignmentStat.put("deadline", assignment.getDeadline());
                assignmentStat.put("averageGrade", Math.round(avgGrade * 100.0) / 100.0);
                assignmentStat.put("highestGrade", highestGrade);
                assignmentStat.put("lowestGrade", lowestGrade);
                assignmentStat.put("submissionRate",
                        Math.round(((double) submittedCount / students.size()) * 100.0) / 100.0);
                assignmentStat.put("onTimeSubmissionRate",
                        Math.round(((double) onTimeCount / students.size()) * 100.0) / 100.0);
                assignmentStat.put("gradeDistribution", calculateGradeDistribution(assignments));

                assignmentsStats.add(assignmentStat);

                // Accumulate overall statistics
                overallAverageGrade += avgGrade * assignments.size();
                totalSubmissions += submittedCount;
                onTimeSubmissions += onTimeCount;
            }
        }

        // Calculate overall statistics
        int totalAssignments = assignmentsList.stream()
                .mapToInt(List::size)
                .sum();

        double overallSubmissionRate = totalAssignments > 0 ?
                (double) totalSubmissions / (students.size() * assignmentsList.size()) * 100 : 0.0;

        double overallOnTimeRate = totalAssignments > 0 ?
                (double) onTimeSubmissions / (students.size() * assignmentsList.size()) * 100 : 0.0;

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
        stats.put("overallOnTimeSubmissionRate", Math.round(overallOnTimeRate * 100.0) / 100.0);

        // Per-assignment statistics
        stats.put("assignmentsStats", assignmentsStats);

        // Attendance statistics
        stats.put("attendanceRate", Math.round(attendanceRate * 100.0) / 100.0);
        stats.put("averageAttendancePerLecture", calculateAverageAttendancePerLecture(lectures));

        return stats;
    }

    private Map<String, Long> calculateGradeDistribution(List<AssignmentSubmission> assignments) {
        return assignments.stream()
                .collect(Collectors.groupingBy(submission -> {
                    float grade = submission.getGrade();
                    if (grade >= 90) return "A (90-100)";
                    else if (grade >= 80) return "B (80-89)";
                    else if (grade >= 70) return "C (70-79)";
                    else if (grade >= 60) return "D (60-69)";
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

            System.out.println("Creating submission rate chart...");
            JFreeChart submissionChart = createSubmissionRateChart(statistics);
            if (submissionChart == null) {
                throw new IllegalStateException("Failed to create submission rate chart");
            }

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

            System.out.println("Drawing charts...");
            try {
                submissionChart.draw(g2d, new Rectangle2D.Double(0, 0, 800, 600));
                gradeChart.draw(g2d, new Rectangle2D.Double(800, 0, 800, 600));
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

    private JFreeChart createSubmissionRateChart(Map<String, Object> statistics) {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            Double submissionRate = convertToDouble(statistics.get("overallSubmissionRate"));
            Double onTimeRate = convertToDouble(statistics.get("overallOnTimeSubmissionRate"));

            dataset.addValue(submissionRate, "Submission Rate", "Overall");
            dataset.addValue(onTimeRate, "On-Time Rate", "Overall");

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
            DefaultPieDataset dataset = new DefaultPieDataset();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> assignmentsStats = (List<Map<String, Object>>) statistics.get("assignmentsStats");

            if (assignmentsStats == null || assignmentsStats.isEmpty()) {
                dataset.setValue("No Data", 1);
                return ChartFactory.createPieChart(
                        "Grade Distribution (No Data)",
                        dataset,
                        true,
                        true,
                        false
                );
            }

            Map<String, Long> totalGradeDistribution = new HashMap<>();  // Changed to Long
            for (Map<String, Object> assignment : assignmentsStats) {
                @SuppressWarnings("unchecked")
                Map<String, Object> distribution = (Map<String, Object>) assignment.get("gradeDistribution");  // Changed to Object
                if (distribution != null) {
                    distribution.forEach((grade, count) -> {
                        Long longCount;
                        if (count instanceof Integer) {
                            longCount = ((Integer) count).longValue();
                        } else if (count instanceof Long) {
                            longCount = (Long) count;
                        } else {
                            longCount = Long.valueOf(count.toString());
                        }
                        totalGradeDistribution.merge(grade, longCount, Long::sum);
                    });
                }
            }

            if (totalGradeDistribution.isEmpty()) {
                dataset.setValue("No Grades", 1);
            } else {
                totalGradeDistribution.forEach((grade, count) ->
                        dataset.setValue(grade, count.doubleValue())  // Convert to double for the dataset
                );
            }

            return ChartFactory.createPieChart(
                    "Grade Distribution",
                    dataset,
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

    private JFreeChart createAssignmentPerformanceChart(Map<String, Object> statistics) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> assignmentsStats = (List<Map<String, Object>>) statistics.get("assignmentsStats");

        for (Map<String, Object> assignment : assignmentsStats) {
            String title = (String) assignment.get("assignmentTitle");
            dataset.addValue((Number) assignment.get("averageGrade"), "Average Grade", title);
            dataset.addValue((Number) assignment.get("highestGrade"), "Highest Grade", title);
            dataset.addValue((Number) assignment.get("lowestGrade"), "Lowest Grade", title);
        }

        return ChartFactory.createLineChart(
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





}