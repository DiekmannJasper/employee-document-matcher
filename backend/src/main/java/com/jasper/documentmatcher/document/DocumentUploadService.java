package com.jasper.documentmatcher.document;

import com.jasper.documentmatcher.storage.DocumentStorage;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentUploadService {

    private final DocumentUploadValidator validator;
    private final DocumentStorage documentStorage;
    private final DocumentRepository documentRepository;

    public DocumentUploadService(
            DocumentUploadValidator validator,
            DocumentStorage documentStorage,
            DocumentRepository documentRepository) {
        this.validator = validator;
        this.documentStorage = documentStorage;
        this.documentRepository = documentRepository;
    }

    public DocumentUploadResponse upload(MultipartFile file) {
        validator.validate(file);

        String storageKey;
        try (var content = file.getInputStream()) {
            storageKey = documentStorage.store(content);
        } catch (IOException e) {
            throw new InvalidUploadException("Die Datei konnte nicht gelesen werden.");
        }

        var document = new Document(
                UUID.randomUUID(), null, file.getOriginalFilename(), storageKey, DocumentStatus.UPLOADED, Instant.now());

        return DocumentUploadResponse.from(documentRepository.save(document));
    }
}
