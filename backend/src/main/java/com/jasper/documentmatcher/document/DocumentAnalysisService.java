package com.jasper.documentmatcher.document;

import com.jasper.documentmatcher.employee.EmployeeRepository;
import com.jasper.documentmatcher.matching.MatchCandidate;
import com.jasper.documentmatcher.matching.PersonMatcher;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
class DocumentAnalysisService {

    private static final BigDecimal DETERMINISTIC_MATCH_SCORE = new BigDecimal("1.0000");

    private final PdfTextExtractor pdfTextExtractor;
    private final PersonMatcher personMatcher;
    private final EmployeeRepository employeeRepository;
    private final DocumentAnalysisRepository documentAnalysisRepository;

    DocumentAnalysisService(
            PdfTextExtractor pdfTextExtractor,
            PersonMatcher personMatcher,
            EmployeeRepository employeeRepository,
            DocumentAnalysisRepository documentAnalysisRepository) {
        this.pdfTextExtractor = pdfTextExtractor;
        this.personMatcher = personMatcher;
        this.employeeRepository = employeeRepository;
        this.documentAnalysisRepository = documentAnalysisRepository;
    }

    DocumentAnalysis analyze(UUID documentId, InputStream pdfContent) {
        var extraction = pdfTextExtractor.extract(pdfContent);

        var analysis = extraction.status() == PdfExtractionStatus.SUCCESS
                ? matchAgainstEmployees(documentId, extraction.text())
                : unreadableDocument(documentId, extraction.status());

        return documentAnalysisRepository.save(analysis);
    }

    private DocumentAnalysis matchAgainstEmployees(UUID documentId, String text) {
        var candidates = employeeRepository.findAll().stream()
                .map(employee -> new MatchCandidate(
                        employee.getId(), employee.getFirstName() + " " + employee.getLastName()))
                .toList();

        var matchResult = personMatcher.match(text, candidates);
        var score = matchResult.status() == MatchStatus.MATCHED ? DETERMINISTIC_MATCH_SCORE : null;

        return new DocumentAnalysis(
                UUID.randomUUID(),
                documentId,
                matchResult.status(),
                matchResult.matchedEmployeeId(),
                score,
                null,
                null,
                matchResult.evidence(),
                ReviewStatus.PENDING,
                Instant.now());
    }

    private DocumentAnalysis unreadableDocument(UUID documentId, PdfExtractionStatus extractionStatus) {
        var evidence =
                switch (extractionStatus) {
                    case EMPTY -> "PDF enthält keinen erkennbaren Text.";
                    case ENCRYPTED -> "PDF ist passwortgeschützt und konnte nicht analysiert werden.";
                    case CORRUPTED -> "PDF konnte nicht gelesen werden.";
                    case SUCCESS -> throw new IllegalStateException("Unexpected SUCCESS in failure path");
                };

        return new DocumentAnalysis(
                UUID.randomUUID(),
                documentId,
                MatchStatus.NO_MATCH,
                null,
                null,
                null,
                null,
                evidence,
                ReviewStatus.PENDING,
                Instant.now());
    }
}
