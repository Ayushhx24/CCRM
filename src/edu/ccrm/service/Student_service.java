package edu.ccrm.service;

import edu.ccrm.domain.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class Student_service implements Data_service<Student> {

    private final List<Student> students = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong(); // For generating unique IDs

    @Override
    public Student add(Student student) {
        // Generate a unique ID for the new student
        student.setId(counter.incrementAndGet());
        students.add(student);
        return student;
    }

    @Override
    public Optional<Student> findById(long id) {
        return students.stream()
                .filter(student -> student.getId() == id)
                .findFirst();
    }

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(students); // Return a copy to prevent modification
    }

    @Override
    public boolean delete(long id) {
        Optional<Student> studentOpt = findById(id);
        if (studentOpt.isPresent()) {
            studentOpt.get().setStatus("Deactivated");
            return true;
        }
        return false;
    }
    
    public Optional<Student> findByRegNo(String regNo) {
        return students.stream()
                .filter(student -> student.getRegNo().equalsIgnoreCase(regNo))
                .findFirst();
    }
    public boolean deleteByRegNo(String regNo) {
        Optional<Student> studentOpt = findByRegNo(regNo);
        if (studentOpt.isPresent()) {
            studentOpt.get().setStatus("Deactivated");
            return true;
        }
        return false;
    }
}
