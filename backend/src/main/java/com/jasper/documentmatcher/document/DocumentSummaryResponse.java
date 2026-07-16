package com.jasper.documentmatcher.document;

import java.time.Instant;
import java.util.UUID;

public record DocumentSummaryResponse(
        UUID id, String originalFilename, UUID categoryId, String contentType, Instant uploadedAt) {

    static DocumentSummaryResponse from(Document document) {
        return new DocumentSummaryResponse(
                document.getId(),
                document.getOriginalFilename(),
                document.getCategoryId(),
                document.getContentType(),
                document.getUploadedAt());
    }
}
