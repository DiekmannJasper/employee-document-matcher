package com.jasper.documentmatcher.document;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees/{employeeId}/documents")
public class EmployeeDocumentController {

    private final EmployeeDocumentService employeeDocumentService;

    public EmployeeDocumentController(EmployeeDocumentService employeeDocumentService) {
        this.employeeDocumentService = employeeDocumentService;
    }

    @GetMapping
    public List<DocumentSummaryResponse> findByEmployee(@PathVariable UUID employeeId) {
        return employeeDocumentService.findByEmployee(employeeId);
    }
}
