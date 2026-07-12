package com.jasper.documentmatcher.document;

import com.jasper.documentmatcher.storage.DocumentStorage;
import java.io.IOException;
import java.time.Instant;
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

    public DocumentUploadService(
            DocumentUploadValidator validator,
            DocumentStorage documentStorage,
            DocumentRepository documentRepository,
            DocumentAnalysisService documentAnalysisService) {
        this.validator = validator;
        this.documentStorage = documentStorage;
        this.documentRepository = documentRepository;
        this.documentAnalysisService = documentAnalysisService;
    }

    /**
     * Document and analysis rows commit together; the filesystem is not a transactional resource,
     * so a failure after {@code store} is compensated by deleting the stored file again.
     */
    @Transactional
    public DocumentUploadResponse upload(MultipartFile file) {
        validator.validate(file);

        String storageKey;
        try (var content = file.getInputStream()) {
            storageKey = documentStorage.store(content);
        } catch (IOException e) {
            throw new InvalidUploadException("Die Datei konnte nicht gelesen werden.");
        }

        try {
            var document = new Document(
                    UUID.randomUUID(),
                    null,
                    file.getOriginalFilename(),
                    storageKey,
                    DocumentStatus.UPLOADED,
                    Instant.now());
            documentRepository.save(document);

            try (var content = file.getInputStream()) {
                documentAnalysisService.analyze(document.getId(), content);
            } catch (IOException e) {
                throw new InvalidUploadException("Die Datei konnte nicht gelesen werden.");
            }

            return DocumentUploadResponse.from(document);
        } catch (RuntimeException e) {
            deleteStoredFileQuietly(storageKey);
            throw e;
        }
    }

    private void deleteStoredFileQuietly(String storageKey) {
        try {
            documentStorage.delete(storageKey);
        } catch (RuntimeException cleanupFailure) {
            log.warn("Could not clean up stored file {} after failed upload", storageKey, cleanupFailure);
        }
    }
}
