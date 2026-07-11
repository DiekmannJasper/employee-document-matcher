package com.jasper.documentmatcher.document;

import java.time.Instant;
import java.util.UUID;

public record DocumentSummaryResponse(UUID id, String originalFilename, UUID categoryId, Instant uploadedAt) {

    static DocumentSummaryResponse from(Document document, DocumentAnalysis analysis) {
        var categoryId = analysis != null ? analysis.getSuggestedCategoryId() : null;
        return new DocumentSummaryResponse(
                document.getId(), document.getOriginalFilename(), categoryId, document.getUploadedAt());
    }
}
