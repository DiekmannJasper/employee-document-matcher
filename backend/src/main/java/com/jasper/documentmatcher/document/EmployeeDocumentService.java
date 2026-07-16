package com.jasper.documentmatcher.document;

import com.jasper.documentmatcher.category.DocumentCategoryService;
import com.jasper.documentmatcher.employee.EmployeeService;
import com.jasper.documentmatcher.storage.DocumentStorage;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class EmployeeDocumentService {

    private final EmployeeService employeeService;
    private final DocumentCategoryService documentCategoryService;
    private final DocumentRepository documentRepository;
    private final DocumentStorage documentStorage;

    EmployeeDocumentService(
            EmployeeService employeeService,
            DocumentCategoryService documentCategoryService,
            DocumentRepository documentRepository,
            DocumentStorage documentStorage) {
        this.employeeService = employeeService;
        this.documentCategoryService = documentCategoryService;
        this.documentRepository = documentRepository;
        this.documentStorage = documentStorage;
    }

    List<DocumentSummaryResponse> findByEmployee(UUID employeeId) {
        employeeService.findById(employeeId);

        return documentRepository.findByEmployeeId(employeeId).stream()
                .map(DocumentSummaryResponse::from)
                .toList();
    }

    DocumentFileResponse openDocument(UUID employeeId, UUID documentId) {
        employeeService.findById(employeeId);

        var document =
                documentRepository.findById(documentId).orElseThrow(() -> new DocumentNotFoundException(documentId));
        if (!Objects.equals(document.getEmployeeId(), employeeId)) {
            throw new DocumentNotFoundException(documentId);
        }

        return new DocumentFileResponse(
                document.getOriginalFilename(), document.getContentType(), documentStorage.load(document.getStorageKey()));
    }

    @Transactional
    DocumentSummaryResponse updateCategory(UUID employeeId, UUID documentId, UUID categoryId) {
        employeeService.findById(employeeId);

        var document =
                documentRepository.findById(documentId).orElseThrow(() -> new DocumentNotFoundException(documentId));
        if (!Objects.equals(document.getEmployeeId(), employeeId)) {
            throw new DocumentNotFoundException(documentId);
        }

        if (categoryId != null) {
            documentCategoryService.findById(categoryId);
        }
        document.assignCategory(categoryId);

        return DocumentSummaryResponse.from(document);
    }
}
