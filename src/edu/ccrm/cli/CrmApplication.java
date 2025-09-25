package edu.ccrm.cli;
import edu.ccrm.domain.*;
import edu.ccrm.exception.DuplicateEnrollmentException;
import edu.ccrm.exception.MaxCreditLimitExceededException;
import edu.ccrm.io.Import_export_service;
import edu.ccrm.service.course_service;
import edu.ccrm.service.Enrollment_service;
import edu.ccrm.service.Student_service;
import edu.ccrm.service.Transcript_service;
import edu.ccrm.io.Backup_service;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CrmApplication {
    private static final Student_service studentService = new Student_service();
    private static final course_service courseService = new course_service();
    private static final Enrollment_service enrollmentService = new Enrollment_service();
    private static final Transcript_service transcriptService = new Transcript_service();
    private static final Import_export_service importExportService = new Import_export_service();
    private static final Backup_service backupService = new Backup_service();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to the Campus Course & Records Manager (CCRM)!");
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = getUserChoice();
            switch (choice) {
                case 1: manageStudents(); break;
                case 2: manageCourses(); break;
                case 3: manageEnrollments(); break;
                case 4: manageFileUtilities(); break;
                case 5: manageReports(); break;
                case 0: running = false; break;
                default: System.out.println("Invalid option. Please try again.");
            }
        }
        System.out.println("Thank you for using CCRM. Goodbye!");
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Manage Students");
        System.out.println("2. Manage Courses");
        System.out.println("3. Enrollment & Grading");
        System.out.println("4. File Utilities");
        System.out.println("5. Reports & Filters");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private static void manageReports() {
        boolean back = false;
        while (!back) {
            printReportsMenu();
            int choice = getUserChoice();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    System.out.print("Filter by (department/semester): ");
                    String criteria = scanner.nextLine();
                    System.out.print("Enter value to filter for: ");
                    String value = scanner.nextLine();
                    
                    List<Course> filteredCourses = courseService.filterCoursesBy(criteria, value);
                    System.out.println("\n--- Filtered Courses ---");
                    if (filteredCourses.isEmpty()) {
                        System.out.println("No courses found matching the criteria.");
                    } else {
                        filteredCourses.forEach(System.out::println);
                    }
                    break;
                case 2: // GPA Distribution Report
                    String report = transcriptService.generateGpaDistributionReport(studentService);
                    System.out.println(report);
                    break;
                    
                case 3: // List students sorted by name
                    System.out.println("\n--- Students Sorted by Name ---");
                    studentService.findAll().stream()
                        .sorted(new java.util.Comparator<Student>() { // Anonymous Inner Class
                            @Override
                            public int compare(Student s1, Student s2) {
                                return s1.getFullName().compareTo(s2.getFullName());
                            }
                        })
                        .forEach(s -> System.out.println(s.getProfile() + "\n"));
                    break;
                    
                case 4: // Find first student in a department
                    System.out.print("Enter department to search for a student in: ");
                    String dept = scanner.nextLine();
                    Student foundStudent = null;
                    
                    search: // This is the label
                    for (Student student : studentService.findAll()) {
                        for (Enrollment enrollment : student.getEnrollments()) {
                            if (enrollment.getCourse().getDepartment().equalsIgnoreCase(dept)) {
                                foundStudent = student;
                                break search; 
                            }
                        }
                    }
                    
                    if (foundStudent != null) {
                        System.out.println("Found first student in " + dept + ":\n" + foundStudent.getProfile());
                    } else {
                        System.out.println("No students found enrolled in the " + dept + " department.");
                    }
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void printReportsMenu() {
        System.out.println("\n--- Reports & Filters ---");
        System.out.println("1. Filter Courses");
        System.out.println("2. Show GPA Distribution Report");
        System.out.println("3. List Students Sorted by Name");
        System.out.println("4. Find First Student in Department");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }
    private static void manageFileUtilities() {
        boolean back = false;
        while (!back) {
            printFileUtilsMenu();
            int choice = getUserChoice();
            scanner.nextLine(); 

            switch (choice) {
                case 1: // Export Data
                    try {
                        System.out.println("Exporting all data...");
                        importExportService.exportData(studentService, courseService, enrollmentService);
                        System.out.println("Data export completed successfully.");
                    } catch (IOException e) {
                        System.err.println("An error occurred during export: " + e.getMessage());
                    }
                    break;
                case 2: // Import Data
                    try {
                        System.out.println("Importing all data from files...");
                        importExportService.importData(studentService, courseService, enrollmentService);
                        System.out.println("Data import completed successfully.");
                    } catch (IOException e) {
                        System.err.println("An error occurred during import: " + e.getMessage());
                    }
                    break;
                case 3: // Create Backup
                    try {
                     
                        importExportService.exportData(studentService, courseService, enrollmentService);
                        Path backupPath = backupService.performBackup();
                        long size = backupService.calculateDirectorySize(backupPath);
                        System.out.printf("Total backup size: %d bytes%n", size);
                    } catch (IOException e) {
                        System.err.println("Backup failed: " + e.getMessage());
                    } catch (RuntimeException e) {
                        System.err.println("Backup failed during file copy: " + e.getMessage());
                    }
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void printFileUtilsMenu() {
        System.out.println("\n--- File Utilities ---");
        System.out.println("1. Export All Data");
        System.out.println("2. Import All Data");
        System.out.println("3. Create Backup & Show Size");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }
    private static int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("That's not a valid number. Please enter a number.");
            scanner.next();
        }
        return scanner.nextInt();
    }
    
    private static void manageStudents() {
        boolean back = false;
        while (!back) {
            printStudentMenu();
            int choice = getUserChoice();
            scanner.nextLine(); 

            switch (choice) {
                case 1: 
                    System.out.print("Enter Full Name: "); String name = scanner.nextLine();
                    System.out.print("Enter Email: "); String email = scanner.nextLine();
                    System.out.print("Enter Registration Number (e.g., S2025001): "); String regNo = scanner.nextLine();
                    Student newStudent = new Student(0, name, email, regNo);
                    studentService.add(newStudent);
                    System.out.println("Student added successfully with ID: " + newStudent.getId());
                    break;
                

                case 2: 
                    System.out.print("Enter Student Registration Number to find: ");
                    String findRegNo = scanner.nextLine();
                    studentService.findByRegNo(findRegNo).ifPresentOrElse(
                        student -> System.out.println("Found: " + student.getProfile()),
                        () -> System.out.println("Student with RegNo " + findRegNo + " not found.")
                    );
                    break;

                case 3:
                    System.out.println("\n--- All Students ---");
                    studentService.findAll().forEach(s -> System.out.println(s.getProfile() + "\n"));
                    break;

                case 4: 
                    System.out.print("Enter Student Registration Number to deactivate: ");
                    String deactivateRegNo = scanner.nextLine();
                    if (studentService.deleteByRegNo(deactivateRegNo)) {
                        System.out.println("Student deactivated successfully.");
                    } else {
                        System.out.println("Student with RegNo " + deactivateRegNo + " not found.");
                    }
                    break;

                case 5: 
                    System.out.print("Enter Student ID for transcript: ");
                    long transcriptId = scanner.nextLong(); 
                    studentService.findById(transcriptId).ifPresentOrElse(
                        student -> System.out.println(transcriptService.generateTranscript(student)),
                        () -> System.out.println("Student not found.")
                    );
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void printStudentMenu() {
        System.out.println("\n--- Student Management ---");
        System.out.println("1. Add New Student");
        System.out.println("2. Find Student by ID");
        System.out.println("3. List All Students");
        System.out.println("4. Deactivate Student");
        System.out.println("5. Print Student Transcript"); 
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }

    private static void manageCourses() {
        boolean back = false;
        while(!back) {
            printCourseMenu();
            int choice = getUserChoice();
            scanner.nextLine();
            switch (choice) {
                 case 1:
                    System.out.print("Enter Course Code (e.g., CS101): "); String code = scanner.nextLine();
                    System.out.print("Enter Course Title: "); String title = scanner.nextLine();
                    System.out.print("Enter Credits: "); int credits = scanner.nextInt(); scanner.nextLine();
                    System.out.print("Enter Department: "); String dept = scanner.nextLine();
                    System.out.print("Enter Semester (SPRING, SUMMER, FALL, WINTER): "); Semester semester = Semester.valueOf(scanner.nextLine().toUpperCase());
                    Course newCourse = new Course.CourseBuilder(code, title).credits(credits).department(dept).semester(semester).build();
                    courseService.add(newCourse);
                    System.out.println("Course added successfully!");
                    break;
                case 2:
                    System.out.print("Enter Course Code to find: "); String findCode = scanner.nextLine();
                    courseService.findByCode(findCode).ifPresentOrElse(
                        course -> System.out.println("Found: " + course.toString()),
                        () -> System.out.println("Course with code " + findCode + " not found."));
                    break;
                case 3:
                    System.out.println("\n--- All Courses ---");
                    courseService.findAll().forEach(System.out::println);
                    break;
                case 4:
                    System.out.print("Enter Course Code to delete: "); String deleteCode = scanner.nextLine();
                    if (courseService.deleteByCode(deleteCode)) System.out.println("Course deleted successfully.");
                    else System.out.println("Course not found.");
                    break;
                case 0: back = true; break;
                default: System.out.println("Invalid option. Please try again.");
            }
        }
    }
    private static void printCourseMenu() {
        System.out.println("\n--- Course Management ---");
        System.out.println("1. Add New Course");
        System.out.println("2. Find Course by Code");
        System.out.println("3. List All Courses");
        System.out.println("4. Delete Course");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }

    private static void manageEnrollments() {
        boolean back = false;
        while (!back) {
            printEnrollmentMenu();
            int choice = getUserChoice();
            scanner.nextLine();

            switch (choice) {
                case 1: 
                    System.out.print("Enter Student ID to enroll: "); long studentId = scanner.nextLong(); scanner.nextLine();
                    System.out.print("Enter Course Code to enroll in: "); String courseCode = scanner.nextLine();
                    Optional<Student> studentOpt = studentService.findById(studentId);
                    Optional<Course> courseOpt = courseService.findByCode(courseCode);
                    if (studentOpt.isPresent() && courseOpt.isPresent()) {
                        Student student = studentOpt.get(); Course course = courseOpt.get();
                        try {
                            enrollmentService.enrollStudent(student, course);
                            System.out.println("Enrollment successful!");
                        } catch (DuplicateEnrollmentException | MaxCreditLimitExceededException e) {
                            System.err.println("Enrollment failed: " + e.getMessage());
                        }
                    } else {
                        if (studentOpt.isEmpty()) System.err.println("Error: Student with ID " + studentId + " not found.");
                        if (courseOpt.isEmpty()) System.err.println("Error: Course with code " + courseCode + " not found.");
                    }
                    break;
                case 2:
                    System.out.println("\n--- All Enrollments ---");
                    enrollmentService.getAllEnrollments().forEach(enrollment -> 
                        System.out.printf("Student: %s (%d) -> Course: %s (%s)%n",
                            enrollment.getStudent().getFullName(), enrollment.getStudent().getId(),
                            enrollment.getCourse().getTitle(), enrollment.getCourse().getCode()));
                    break;
                
                case 3: // Record Grade
                    System.out.print("Enter Student ID: ");
                    long gradeStudentId = scanner.nextLong();
                    scanner.nextLine();
                    System.out.print("Enter Course Code: ");
                    String gradeCourseCode = scanner.nextLine();

                    enrollmentService.findEnrollment(gradeStudentId, gradeCourseCode).ifPresentOrElse(
                        enrollment -> {
                            System.out.print("Enter Letter Grade (S, A, B, C, D, E, F): ");
                            String gradeStr = scanner.nextLine().toUpperCase();
                            try {
                                Grade grade = Grade.valueOf(gradeStr);
                                enrollment.setGrade(grade);
                                System.out.println("Grade recorded successfully.");
                            } catch (IllegalArgumentException e) {
                                System.err.println("Invalid grade entered. Please use one of S, A, B, C, D, E, F.");
                            }
                        },
                        () -> System.out.println("Enrollment record not found.")
                    );
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void printEnrollmentMenu() {
        System.out.println("\n--- Enrollment Management ---");
        System.out.println("1. Enroll Student in Course");
        System.out.println("2. List All Enrollments");
        System.out.println("3. Record Grade for Enrollment");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }
}