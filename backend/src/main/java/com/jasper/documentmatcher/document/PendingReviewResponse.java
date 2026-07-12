package com.jasper.documentmatcher.document;

import com.jasper.documentmatcher.confidence.ConfidenceBandCalculator;
import com.jasper.documentmatcher.confidence.ConfidenceLevel;
import java.time.Instant;
import java.util.UUID;

/**
 * systemScore and llmConfidence are qualitative bands (see {@link ConfidenceLevel}), never raw
 * scores - the deterministic person-match signal and the classifier's category signal are kept
 * explicitly separate rather than combined into one number.
 */
public record PendingReviewResponse(
        UUID documentId,
        String originalFilename,
        MatchStatus matchStatus,
        UUID suggestedEmployeeId,
        String evidence,
        ConfidenceLevel systemScore,
        UUID suggestedCategoryId,
        String suggestedCategoryName,
        String categoryEvidence,
        ConfidenceLevel llmConfidence,
        Instant uploadedAt) {

    static PendingReviewResponse from(
            Document document, DocumentAnalysis analysis, ConfidenceBandCalculator confidenceBandCalculator) {
        return new PendingReviewResponse(
                document.getId(),
                document.getOriginalFilename(),
                analysis.getMatchStatus(),
                analysis.getMatchedEmployeeId(),
                analysis.getEvidence(),
                confidenceBandCalculator.bandFor(analysis.getMatchScore()),
                analysis.getSuggestedCategoryId(),
                analysis.getSuggestedCategoryName(),
                analysis.getCategoryEvidence(),
                confidenceBandCalculator.bandFor(analysis.getCategoryConfidence()),
                document.getUploadedAt());
    }
}
