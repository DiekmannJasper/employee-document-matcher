package com.jasper.documentmatcher.document;

import java.time.Instant;
import java.util.UUID;

public record PendingReviewResponse(
        UUID documentId,
        String originalFilename,
        MatchStatus matchStatus,
        UUID suggestedEmployeeId,
        String evidence,
        Instant uploadedAt) {

    static PendingReviewResponse from(Document document, DocumentAnalysis analysis) {
        return new PendingReviewResponse(
                document.getId(),
                document.getOriginalFilename(),
                analysis.getMatchStatus(),
                analysis.getMatchedEmployeeId(),
                analysis.getEvidence(),
                document.getUploadedAt());
    }
}
