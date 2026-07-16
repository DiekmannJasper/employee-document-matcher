package com.jasper.documentmatcher.document;

import java.util.List;
import java.util.UUID;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/{documentId}/file")
    public ResponseEntity<InputStreamResource> openDocument(
            @PathVariable UUID employeeId, @PathVariable UUID documentId) {
        var document = employeeDocumentService.openDocument(employeeId, documentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.contentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(document.filename()).build().toString())
                .body(new InputStreamResource(document.content()));
    }

    @PatchMapping("/{documentId}/category")
    public DocumentSummaryResponse updateCategory(
            @PathVariable UUID employeeId,
            @PathVariable UUID documentId,
            @RequestBody UpdateDocumentCategoryRequest request) {
        return employeeDocumentService.updateCategory(employeeId, documentId, request.categoryId());
    }
}
