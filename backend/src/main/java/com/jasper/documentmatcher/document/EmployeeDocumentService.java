package com.jasper.documentmatcher.document;

import com.jasper.documentmatcher.employee.EmployeeService;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
class EmployeeDocumentService {

    private final EmployeeService employeeService;
    private final DocumentRepository documentRepository;
    private final DocumentAnalysisRepository documentAnalysisRepository;

    EmployeeDocumentService(
            EmployeeService employeeService,
            DocumentRepository documentRepository,
            DocumentAnalysisRepository documentAnalysisRepository) {
        this.employeeService = employeeService;
        this.documentRepository = documentRepository;
        this.documentAnalysisRepository = documentAnalysisRepository;
    }

    List<DocumentSummaryResponse> findByEmployee(UUID employeeId) {
        employeeService.findById(employeeId);

        var documents = documentRepository.findByEmployeeId(employeeId);
        var documentIds = documents.stream().map(Document::getId).toList();
        var analysesByDocumentId = documentAnalysisRepository.findByDocumentIdIn(documentIds).stream()
                .collect(Collectors.toMap(DocumentAnalysis::getDocumentId, Function.identity()));

        return documents.stream()
                .map(document -> DocumentSummaryResponse.from(document, analysesByDocumentId.get(document.getId())))
                .toList();
    }
}
