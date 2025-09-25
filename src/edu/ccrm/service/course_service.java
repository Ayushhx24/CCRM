package edu.ccrm.service;

import edu.ccrm.domain.Course;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import edu.ccrm.domain.Semester;
import java.util.stream.Stream;

public class course_service implements Data_service<Course> {

    private final List<Course> courses = new ArrayList<>();

    @Override
    public Course add(Course course) {
        courses.add(course);
        return course;
    }

    @Override
    public Optional<Course> findById(long id) {
        return Optional.empty();
    }
    
    public Optional<Course> findByCode(String code) {
        return courses.stream()
                .filter(course -> course.getCode().equalsIgnoreCase(code))
                .findFirst();
    }

    @Override
    public List<Course> findAll() {
        return new ArrayList<>(courses);
    }

    @Override
    public boolean delete(long id) {
        return false;
    }
    
    public boolean deleteByCode(String code) {
        return courses.removeIf(course -> course.getCode().equalsIgnoreCase(code));
    }
    
    public List<Course> filterCoursesBy(String criteria, String value) {
        Stream<Course> courseStream = courses.stream();
        
        switch (criteria.toLowerCase()) {
            case "department":
                return courseStream
                    .filter(c -> c.getDepartment().equalsIgnoreCase(value))
                    .collect(Collectors.toList());
            case "semester":
                try {
                    Semester semester = Semester.valueOf(value.toUpperCase());
                    return courseStream
                        .filter(c -> c.getSemester() == semester)
                        .collect(Collectors.toList());
                } catch (IllegalArgumentException e) {
                    return List.of();
                }
            default:
                return List.of(); 
        }
    }
}