package com.jasper.documentmatcher.employee;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll().stream().map(EmployeeResponse::from).toList();
    }

    public EmployeeResponse findById(UUID employeeId) {
        return employeeRepository
                .findById(employeeId)
                .map(EmployeeResponse::from)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    }
}
