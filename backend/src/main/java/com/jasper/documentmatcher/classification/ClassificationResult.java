package com.jasper.documentmatcher.classification;

import java.math.BigDecimal;
import java.util.UUID;

public record ClassificationResult(
        ClassificationAction action,
        UUID categoryId,
        String suggestedCategoryName,
        BigDecimal confidence,
        String evidence,
        String reasoning) {

    static ClassificationResult useExisting(CategoryCandidate candidate, BigDecimal confidence, String evidence) {
        return new ClassificationResult(
                ClassificationAction.USE_EXISTING,
                candidate.categoryId(),
                null,
                confidence,
                evidence,
                "Dokument passt zu bestehender Kategorie '" + candidate.displayName() + "'.");
    }

    static ClassificationResult suggestNew(String categoryName, BigDecimal confidence, String evidence) {
        return new ClassificationResult(
                ClassificationAction.SUGGEST_NEW,
                null,
                categoryName,
                confidence,
                evidence,
                "Neue Kategorie '" + categoryName + "' vorgeschlagen, da keine passende bestehende Kategorie existiert.");
    }

    static ClassificationResult manualReview(String reasoning) {
        return new ClassificationResult(ClassificationAction.MANUAL_REVIEW, null, null, null, null, reasoning);
    }
}
