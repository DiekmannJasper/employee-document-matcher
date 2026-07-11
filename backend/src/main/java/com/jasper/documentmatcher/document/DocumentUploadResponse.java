package com.jasper.documentmatcher.document;

import java.time.Instant;
import java.util.UUID;

public record DocumentUploadResponse(
        UUID id, String originalFilename, DocumentStatus status, Instant uploadedAt) {

    static DocumentUploadResponse from(Document document) {
        return new DocumentUploadResponse(
                document.getId(), document.getOriginalFilename(), document.getStatus(), document.getUploadedAt());
    }
}
