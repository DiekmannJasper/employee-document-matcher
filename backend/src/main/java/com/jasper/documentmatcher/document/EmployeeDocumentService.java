package com.jasper.documentmatcher.document;

import com.jasper.documentmatcher.employee.EmployeeService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
class EmployeeDocumentService {

    private final EmployeeService employeeService;
    private final DocumentRepository documentRepository;

    EmployeeDocumentService(EmployeeService employeeService, DocumentRepository documentRepository) {
        this.employeeService = employeeService;
        this.documentRepository = documentRepository;
    }

    List<DocumentSummaryResponse> findByEmployee(UUID employeeId) {
        employeeService.findById(employeeId);

        return documentRepository.findByEmployeeId(employeeId).stream()
                .map(DocumentSummaryResponse::from)
                .toList();
    }
}
