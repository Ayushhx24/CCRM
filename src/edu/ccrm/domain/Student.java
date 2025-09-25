package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Student extends Person {

    private String regNo;
    private String status;
    private LocalDate enrollmentDate;
    private List<Enrollment> enrollments = new ArrayList<>();

    public Student(long id, String fullName, String email, String regNo) {
        setId(id);
        setFullName(fullName);
        setEmail(email);
        this.regNo = regNo;
        this.status = "Active";
        this.enrollmentDate = LocalDate.now();
    }

    @Override
    public String getProfile() {
        String enrolledCoursesStr = enrollments.stream()
            .map(e -> e.getCourse().getCode())
            .collect(Collectors.joining(", "));
        return String.format("Student Profile:%n  ID: %d%n  Name: %s%n  Reg No: %s%n  Status: %s%n  Enrolled In: [%s]",
                getId(), getFullName(), getRegNo(), getStatus(), enrolledCoursesStr);
    }

    // Method to add an enrollment to the student's record
    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
    }

    // Helper to calculate total credits for a specific semester
    public int getTotalCreditsForSemester(Semester semester) {
        return enrollments.stream()
            .filter(e -> e.getCourse().getSemester() == semester)
            .mapToInt(e -> e.getCourse().getCredits())
            .sum();
    }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public List<Enrollment> getEnrollments() { return enrollments; }
}