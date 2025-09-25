package edu.ccrm.io;

import edu.ccrm.domain.*;
import edu.ccrm.exception.DuplicateEnrollmentException;
import edu.ccrm.exception.MaxCreditLimitExceededException;
import edu.ccrm.service.course_service;
import edu.ccrm.service.Enrollment_service;
import edu.ccrm.service.Student_service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Import_export_service {

    private static final String DATA_DIRECTORY = "data";
    private static final Path STUDENTS_FILE = Paths.get(DATA_DIRECTORY, "students.csv");
    private static final Path COURSES_FILE = Paths.get(DATA_DIRECTORY, "courses.csv");
    private static final Path ENROLLMENTS_FILE = Paths.get(DATA_DIRECTORY, "enrollments.csv");

    public void exportData(Student_service studentService, course_service courseService, Enrollment_service enrollmentService) throws IOException {
        Files.createDirectories(Paths.get(DATA_DIRECTORY));

        List<String> studentLines = studentService.findAll().stream()
            .map(s -> String.join(",", String.valueOf(s.getId()), s.getRegNo(), "\"" + s.getFullName() + "\"", s.getEmail(), s.getStatus()))
            .collect(Collectors.toList());
        Files.write(STUDENTS_FILE, studentLines);
        System.out.println("Exported " + studentLines.size() + " students to " + STUDENTS_FILE);

        List<String> courseLines = courseService.findAll().stream()
            .map(c -> String.join(",", c.getCode(), "\"" + c.getTitle() + "\"", String.valueOf(c.getCredits()), c.getDepartment(), c.getSemester().name()))
            .collect(Collectors.toList());
        Files.write(COURSES_FILE, courseLines);
        System.out.println("Exported " + courseLines.size() + " courses to " + COURSES_FILE);

        List<String> enrollmentLines = enrollmentService.getAllEnrollments().stream()
            .map(e -> String.join(",", String.valueOf(e.getStudent().getId()), e.getCourse().getCode(), e.getGrade() != null ? e.getGrade().name() : "NO_GRADE"))
            .collect(Collectors.toList());
        Files.write(ENROLLMENTS_FILE, enrollmentLines);
        System.out.println("Exported " + enrollmentLines.size() + " enrollments to " + ENROLLMENTS_FILE);
    }

    public void importData(Student_service studentService, course_service courseService, Enrollment_service enrollmentService) throws IOException {
        if (Files.exists(STUDENTS_FILE)) {
            try (Stream<String> lines = Files.lines(STUDENTS_FILE)) {
                lines.forEach(line -> {
                    String[] parts = line.split(",", 5);
                    Student student = new Student(Long.parseLong(parts[0]), parts[2].replaceAll("\"", ""), parts[3], parts[1]);
                    student.setStatus(parts[4]);
                    studentService.add(student);
                });
                System.out.println("Imported students from " + STUDENTS_FILE);
            }
        }

        if (Files.exists(COURSES_FILE)) {
            try (Stream<String> lines = Files.lines(COURSES_FILE)) {
                lines.forEach(line -> {
                    String[] parts = line.split(",", 5);
                    Course course = new Course.CourseBuilder(parts[0], parts[1].replaceAll("\"", ""))
                        .credits(Integer.parseInt(parts[2]))
                        .department(parts[3])
                        .semester(Semester.valueOf(parts[4]))
                        .build();
                    courseService.add(course);
                });
                System.out.println("Imported courses from " + COURSES_FILE);
            }
        }
        
        if (Files.exists(ENROLLMENTS_FILE)) {
            try (Stream<String> lines = Files.lines(ENROLLMENTS_FILE)) {
                lines.forEach(line -> {
                    String[] parts = line.split(",");
                    long studentId = Long.parseLong(parts[0]);
                    String courseCode = parts[1];
                    String gradeStr = parts[2];

                    Optional<Student> studentOpt = studentService.findById(studentId);
                    Optional<Course> courseOpt = courseService.findByCode(courseCode);

                    if (studentOpt.isPresent() && courseOpt.isPresent()) {
                        try {
                            enrollmentService.enrollStudent(studentOpt.get(), courseOpt.get());
                            if (!gradeStr.equals("NO_GRADE")) {
                                enrollmentService.findEnrollment(studentId, courseCode)
                                    .ifPresent(e -> e.setGrade(Grade.valueOf(gradeStr)));
                            }
                        } catch (DuplicateEnrollmentException | MaxCreditLimitExceededException e) {
                             System.err.println("Skipping duplicate/invalid enrollment: " + e.getMessage());
                        }
                    }
                });
                System.out.println("Imported enrollments from " + ENROLLMENTS_FILE);
            }
        }
    }
}
