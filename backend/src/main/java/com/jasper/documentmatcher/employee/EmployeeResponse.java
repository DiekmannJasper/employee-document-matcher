package com.jasper.documentmatcher.employee;

import java.util.UUID;

public record EmployeeResponse(
        UUID id, String personnelNumber, String firstName, String lastName, String department) {

    static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getPersonnelNumber(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDepartment());
    }
}
