package com.jasper.documentmatcher.document;

import com.jasper.documentmatcher.category.DocumentCategoryRepository;
import com.jasper.documentmatcher.employee.Employee;
import com.jasper.documentmatcher.employee.EmployeeRepository;
import com.jasper.documentmatcher.storage.DocumentStorage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentUploadService {

    private static final Logger log = LoggerFactory.getLogger(DocumentUploadService.class);

    private final DocumentUploadValidator validator;
    private final DocumentStorage documentStorage;
    private final DocumentRepository documentRepository;
    private final DocumentAnalysisService documentAnalysisService;
    private final EmployeeRepository employeeRepository;
    private final DocumentCategoryRepository documentCategoryRepository;

    public DocumentUploadService(
            DocumentUploadValidator validator,
            DocumentStorage documentStorage,
            DocumentRepository documentRepository,
            DocumentAnalysisService documentAnalysisService,
            EmployeeRepository employeeRepository,
            DocumentCategoryRepository documentCategoryRepository) {
        this.validator = validator;
        this.documentStorage = documentStorage;
        this.documentRepository = documentRepository;
        this.documentAnalysisService = documentAnalysisService;
        this.employeeRepository = employeeRepository;
        this.documentCategoryRepository = documentCategoryRepository;
    }

    /**
     * Document and analysis rows commit together; the filesystem is not a transactional resource,
     * so a failure after {@code store} is compensated by deleting the stored file again.
     */
    @Transactional
    public DocumentUploadResponse upload(MultipartFile file) {
        validator.validate(file);

        try (var content = file.getInputStream()) {
            return ingest(file.getOriginalFilename(), content);
        } catch (IOException e) {
            throw new InvalidUploadException("Die Datei konnte nicht gelesen werden.");
        }
    }

    @Transactional
    public DocumentUploadResponse importExternal(ExternalDocumentContent externalDocument) {
        return ingest(externalDocument.filename(), new ByteArrayInputStream(externalDocument.content()));
    }

    private DocumentUploadResponse ingest(String originalFilename, InputStream content) {
        byte[] bytes;
        try {
            bytes = content.readAllBytes();
            if (bytes.length == 0) {
                throw new InvalidUploadException("Es wird genau eine Datei erwartet.");
            }
        } catch (IOException e) {
            throw new InvalidUploadException("Die Datei konnte nicht gelesen werden.");
        }

        var format = DocumentFormat.detect(bytes)
                .orElseThrow(() -> new InvalidUploadException("Nur PDF-, Word- (.docx) und XML-Dateien werden unterstützt."));
        var storageKey = documentStorage.store(new ByteArrayInputStream(bytes), format.extension());

        try {
            var document = new Document(
                    UUID.randomUUID(),
                    null,
                    originalFilename,
                    storageKey,
                    format.contentType(),
                    DocumentStatus.UPLOADED,
                    Instant.now());
            documentRepository.save(document);

            var analysis = documentAnalysisService.analyze(document.getId(), format, new ByteArrayInputStream(bytes));
            autoAssignIfMatched(document, analysis);

            return uploadResponse(document);
        } catch (RuntimeException e) {
            deleteStoredFileQuietly(storageKey);
            throw e;
        }
    }

    private void autoAssignIfMatched(Document document, DocumentAnalysis analysis) {
        if (analysis.getMatchStatus() != MatchStatus.MATCHED || analysis.getMatchedEmployeeId() == null) {
            return;
        }

        document.assignToEmployee(analysis.getMatchedEmployeeId());
        if (analysis.getSuggestedCategoryId() != null) {
            document.assignCategory(analysis.getSuggestedCategoryId());
        }
        analysis.confirm();
        documentRepository.save(document);
    }

    private DocumentUploadResponse uploadResponse(Document document) {
        if (document.getStatus() != DocumentStatus.ASSIGNED) {
            return DocumentUploadResponse.from(document);
        }

        var employeeName = employeeRepository.findById(document.getEmployeeId()).map(this::fullName).orElse(null);
        var categoryName = Optional.ofNullable(document.getCategoryId())
                .flatMap(documentCategoryRepository::findById)
                .map(category -> category.getDisplayName())
                .orElse(null);

        return DocumentUploadResponse.from(document, employeeName, categoryName);
    }

    private String fullName(Employee employee) {
        return employee.getFirstName() + " " + employee.getLastName();
    }

    private void deleteStoredFileQuietly(String storageKey) {
        try {
            documentStorage.delete(storageKey);
        } catch (RuntimeException cleanupFailure) {
            log.warn("Could not clean up stored file {} after failed upload", storageKey, cleanupFailure);
        }
    }
}
