package com.jasper.documentmatcher.document;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "document_analyses")
public class DocumentAnalysis {

    @Id
    private UUID id;

    @Column(name = "document_id", nullable = false, unique = true)
    private UUID documentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_status", nullable = false, length = 20)
    private MatchStatus matchStatus;

    @Column(name = "matched_employee_id")
    private UUID matchedEmployeeId;

    @Column(name = "match_score")
    private BigDecimal matchScore;

    @Column(name = "suggested_category_id")
    private UUID suggestedCategoryId;

    @Column(name = "suggested_category_name")
    private String suggestedCategoryName;

    @Column(name = "category_confidence")
    private BigDecimal categoryConfidence;

    @Column
    private String evidence;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false, length = 20)
    private ReviewStatus reviewStatus;

    @Column(name = "analyzed_at", nullable = false, updatable = false)
    private Instant analyzedAt;

    protected DocumentAnalysis() {
    }

    DocumentAnalysis(
            UUID id,
            UUID documentId,
            MatchStatus matchStatus,
            UUID matchedEmployeeId,
            BigDecimal matchScore,
            UUID suggestedCategoryId,
            String suggestedCategoryName,
            BigDecimal categoryConfidence,
            String evidence,
            ReviewStatus reviewStatus,
            Instant analyzedAt) {
        this.id = id;
        this.documentId = documentId;
        this.matchStatus = matchStatus;
        this.matchedEmployeeId = matchedEmployeeId;
        this.matchScore = matchScore;
        this.suggestedCategoryId = suggestedCategoryId;
        this.suggestedCategoryName = suggestedCategoryName;
        this.categoryConfidence = categoryConfidence;
        this.evidence = evidence;
        this.reviewStatus = reviewStatus;
        this.analyzedAt = analyzedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public MatchStatus getMatchStatus() {
        return matchStatus;
    }

    public UUID getMatchedEmployeeId() {
        return matchedEmployeeId;
    }

    public BigDecimal getMatchScore() {
        return matchScore;
    }

    public UUID getSuggestedCategoryId() {
        return suggestedCategoryId;
    }

    public String getSuggestedCategoryName() {
        return suggestedCategoryName;
    }

    public BigDecimal getCategoryConfidence() {
        return categoryConfidence;
    }

    public String getEvidence() {
        return evidence;
    }

    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public Instant getAnalyzedAt() {
        return analyzedAt;
    }

    void confirm() {
        this.reviewStatus = ReviewStatus.CONFIRMED;
    }
}
