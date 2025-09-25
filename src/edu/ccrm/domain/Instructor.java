package edu.ccrm.domain;
public class Instructor extends Person {

    private String employeeId;
    private String department;

    public Instructor(long id, String fullName, String email, String employeeId) {
        setId(id);
        setFullName(fullName);
        setEmail(email);
        this.employeeId = employeeId;
    }

    @Override
    public String getProfile() {
        return String.format("Instructor Profile:%nID: %d%nName: %s%nEmployee ID: %s",
                getId(), getFullName(), getEmployeeId());
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
