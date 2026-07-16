package com.jasper.documentmatcher.document;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/external-documents")
public class ExternalDocumentController {

    private final ExternalDocumentProvider externalDocumentProvider;
    private final DocumentUploadService documentUploadService;

    public ExternalDocumentController(
            ExternalDocumentProvider externalDocumentProvider, DocumentUploadService documentUploadService) {
        this.externalDocumentProvider = externalDocumentProvider;
        this.documentUploadService = documentUploadService;
    }

    @GetMapping
    public List<ExternalDocumentResponse> findAvailableDocuments() {
        return externalDocumentProvider.findAvailableDocuments();
    }

    @PostMapping("/imports")
    public ResponseEntity<ExternalDocumentImportResponse> importDocument(@RequestBody ExternalDocumentImportRequest request) {
        var documents = externalDocumentProvider.fetchAll(request.externalDocumentId()).stream()
                .map(documentUploadService::importExternal)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(new ExternalDocumentImportResponse(documents));
    }
}
