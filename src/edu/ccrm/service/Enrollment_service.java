package edu.ccrm.service;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Enrollment;
import edu.ccrm.domain.Student;
import edu.ccrm.exception.DuplicateEnrollmentException;
import edu.ccrm.exception.MaxCreditLimitExceededException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Import Optional

public class Enrollment_service {

    private final List<Enrollment> allEnrollments = new ArrayList<>();
    public static final int MAX_CREDITS_PER_SEMESTER = 21;

    public void enrollStudent(Student student, Course course) 
            throws DuplicateEnrollmentException, MaxCreditLimitExceededException {
        boolean alreadyEnrolled = student.getEnrollments().stream()
            .anyMatch(e -> e.getCourse().getCode().equals(course.getCode()));
        if (alreadyEnrolled) {
            throw new DuplicateEnrollmentException(
                "Student " + student.getFullName() + " is already enrolled in " + course.getCode()
            );
        }
        int currentCredits = student.getTotalCreditsForSemester(course.getSemester());
        if (currentCredits + course.getCredits() > MAX_CREDITS_PER_SEMESTER) {
            throw new MaxCreditLimitExceededException(
                "Cannot enroll. Exceeds max credit limit of " + MAX_CREDITS_PER_SEMESTER + " for the semester."
            );
        }
        Enrollment newEnrollment = new Enrollment(student, course);
        allEnrollments.add(newEnrollment);
        student.addEnrollment(newEnrollment);
    }

    public List<Enrollment> getAllEnrollments() {
        return new ArrayList<>(allEnrollments);
    }
    
    public Optional<Enrollment> findEnrollment(long studentId, String courseCode) {
        return allEnrollments.stream()
            .filter(e -> e.getStudent().getId() == studentId && e.getCourse().getCode().equalsIgnoreCase(courseCode))
            .findFirst();
    }
}