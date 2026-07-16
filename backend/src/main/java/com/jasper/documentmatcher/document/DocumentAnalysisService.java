package com.jasper.documentmatcher.document;

import com.jasper.documentmatcher.category.DocumentCategoryRepository;
import com.jasper.documentmatcher.classification.CategoryCandidate;
import com.jasper.documentmatcher.classification.ClassificationResult;
import com.jasper.documentmatcher.classification.DocumentClassifier;
import com.jasper.documentmatcher.employee.EmployeeRepository;
import com.jasper.documentmatcher.matching.MatchCandidate;
import com.jasper.documentmatcher.matching.PersonMatchResult;
import com.jasper.documentmatcher.matching.PersonMatcher;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
class DocumentAnalysisService {

    private static final BigDecimal DETERMINISTIC_MATCH_SCORE = new BigDecimal("1.0000");
    private static final BigDecimal AMBIGUOUS_MATCH_SCORE = new BigDecimal("0.5000");

    private final DocumentTextExtractorResolver textExtractorResolver;
    private final PersonMatcher personMatcher;
    private final DocumentClassifier documentClassifier;
    private final EmployeeRepository employeeRepository;
    private final DocumentCategoryRepository documentCategoryRepository;
    private final DocumentAnalysisRepository documentAnalysisRepository;

    DocumentAnalysisService(
            DocumentTextExtractorResolver textExtractorResolver,
            PersonMatcher personMatcher,
            DocumentClassifier documentClassifier,
            EmployeeRepository employeeRepository,
            DocumentCategoryRepository documentCategoryRepository,
            DocumentAnalysisRepository documentAnalysisRepository) {
        this.textExtractorResolver = textExtractorResolver;
        this.personMatcher = personMatcher;
        this.documentClassifier = documentClassifier;
        this.employeeRepository = employeeRepository;
        this.documentCategoryRepository = documentCategoryRepository;
        this.documentAnalysisRepository = documentAnalysisRepository;
    }

    DocumentAnalysis analyze(UUID documentId, DocumentFormat format, InputStream content) {
        var extraction = textExtractorResolver.extract(format, content);

        var analysis = extraction.status() == DocumentExtractionStatus.SUCCESS
                ? analyzeReadableDocument(documentId, extraction.text())
                : unreadableDocument(documentId, extraction.status());

        return documentAnalysisRepository.save(analysis);
    }

    private DocumentAnalysis analyzeReadableDocument(UUID documentId, String text) {
        var matchResult = matchPerson(text);
        var classification = classifyCategory(text);
        var score =
                switch (matchResult.status()) {
                    case MATCHED -> DETERMINISTIC_MATCH_SCORE;
                    case AMBIGUOUS -> AMBIGUOUS_MATCH_SCORE;
                    case NO_MATCH, UNREADABLE -> null;
                };
        var categoryEvidence =
                classification.evidence() != null ? classification.evidence() : classification.reasoning();

        return new DocumentAnalysis(
                UUID.randomUUID(),
                documentId,
                matchResult.status(),
                matchResult.matchedEmployeeId(),
                score,
                classification.categoryId(),
                classification.suggestedCategoryName(),
                classification.confidence(),
                categoryEvidence,
                matchResult.evidence(),
                ReviewStatus.PENDING,
                Instant.now());
    }

    private PersonMatchResult matchPerson(String text) {
        var candidates = employeeRepository.findAll().stream()
                .map(employee -> new MatchCandidate(
                        employee.getId(), employee.getFirstName() + " " + employee.getLastName()))
                .toList();

        return personMatcher.match(text, candidates);
    }

    private ClassificationResult classifyCategory(String text) {
        var candidates = documentCategoryRepository.findAll().stream()
                .map(category -> new CategoryCandidate(category.getId(), category.getCode(), category.getDisplayName()))
                .toList();

        return documentClassifier.classify(text, candidates);
    }

    private DocumentAnalysis unreadableDocument(UUID documentId, DocumentExtractionStatus extractionStatus) {
        var evidence =
                switch (extractionStatus) {
                    case EMPTY -> "Datei enthält keinen erkennbaren Text.";
                    case ENCRYPTED -> "Datei ist passwortgeschützt und konnte nicht analysiert werden.";
                    case CORRUPTED -> "Datei konnte nicht gelesen werden.";
                    case SUCCESS -> throw new IllegalStateException("Unexpected SUCCESS in failure path");
                };

        return new DocumentAnalysis(
                UUID.randomUUID(),
                documentId,
                MatchStatus.UNREADABLE,
                null,
                null,
                null,
                null,
                null,
                null,
                evidence,
                ReviewStatus.PENDING,
                Instant.now());
    }
}
