package com.jasper.documentmatcher.document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PendingReviewResponse(
        UUID documentId,
        String originalFilename,
        MatchStatus matchStatus,
        UUID suggestedEmployeeId,
        String evidence,
        UUID suggestedCategoryId,
        String suggestedCategoryName,
        BigDecimal categoryConfidence,
        Instant uploadedAt) {

    static PendingReviewResponse from(Document document, DocumentAnalysis analysis) {
        return new PendingReviewResponse(
                document.getId(),
                document.getOriginalFilename(),
                analysis.getMatchStatus(),
                analysis.getMatchedEmployeeId(),
                analysis.getEvidence(),
                analysis.getSuggestedCategoryId(),
                analysis.getSuggestedCategoryName(),
                analysis.getCategoryConfidence(),
                document.getUploadedAt());
    }
}
