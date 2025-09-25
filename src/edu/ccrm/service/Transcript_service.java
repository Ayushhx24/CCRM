package edu.ccrm.service;

import edu.ccrm.domain.Enrollment;
import edu.ccrm.domain.Student;
import java.text.DecimalFormat;
import java.util.stream.Collectors;
import java.util.Map;

public class Transcript_service {

    public double calculateGpa(Student student) {
        double totalPoints = 0.0;
        int totalCredits = 0;

        for (Enrollment enrollment : student.getEnrollments()) {
            if (enrollment.getGrade() != null) {
                int credits = enrollment.getCourse().getCredits();
                totalPoints += enrollment.getGrade().getPoints() * credits;
                totalCredits += credits;
            }
        }
        if (totalCredits == 0) {
            return 0.0;
        }

        return totalPoints / totalCredits;
    }
    public String generateTranscript(Student student) {
        StringBuilder transcript = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.##");

        transcript.append("--- TRANSCRIPT ---\n");
        transcript.append(student.getProfile()).append("\n\n");
        transcript.append("Courses and Grades:\n");

        if (student.getEnrollments().isEmpty()) {
            transcript.append("  No courses enrolled.\n");
        } else {
            for (Enrollment e : student.getEnrollments()) {
                String grade = (e.getGrade() != null) ? e.getGrade().toString() : "Not Graded";
                transcript.append(String.format("  - %s: %s (%d credits) - Grade: %s\n",
                    e.getCourse().getCode(), e.getCourse().getTitle(), e.getCourse().getCredits(), grade));
            }
        }

        transcript.append("\nOverall GPA: ").append(df.format(calculateGpa(student)));
        transcript.append("\n--- END OF TRANSCRIPT ---\n");

        return transcript.toString();
    }
    
    public String generateGpaDistributionReport(Student_service studentService) {
        Map<String, Long> gpaDistribution = studentService.findAll().stream()
            .collect(Collectors.groupingBy(
                student -> {
                    double gpa = calculateGpa(student);
                    if (gpa >= 9.0) return "9.0 - 10.0 (S/A)";
                    if (gpa >= 8.0) return "8.0 - 8.9 (B)";
                    if (gpa >= 7.0) return "7.0 - 7.9 (C)";
                    if (gpa >= 6.0) return "6.0 - 6.9 (D)";
                    if (gpa >= 5.0) return "5.0 - 5.9 (E)";
                    return "Below 5.0 (F)";
                },
                Collectors.counting()
            ));

        StringBuilder report = new StringBuilder();
        report.append("--- GPA Distribution Report ---\n");
        gpaDistribution.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> 
                report.append(String.format("  %s: %d student(s)\n", entry.getKey(), entry.getValue()))
            );
        report.append("--- End of Report ---\n");
        return report.toString();
    }
}
