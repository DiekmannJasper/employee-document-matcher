package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.jasper.documentmatcher.category.DocumentCategoryRepository;
import com.jasper.documentmatcher.classification.ClassificationAction;
import com.jasper.documentmatcher.classification.ClassificationResult;
import com.jasper.documentmatcher.classification.DocumentClassifier;
import com.jasper.documentmatcher.employee.EmployeeRepository;
import com.jasper.documentmatcher.matching.PersonMatchResult;
import com.jasper.documentmatcher.matching.PersonMatcher;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentAnalysisServiceTest {

    @Mock private PdfTextExtractor pdfTextExtractor;
    @Mock private PersonMatcher personMatcher;
    @Mock private DocumentClassifier documentClassifier;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private DocumentCategoryRepository documentCategoryRepository;
    @Mock private DocumentAnalysisRepository documentAnalysisRepository;

    private DocumentAnalysisService service() {
        return new DocumentAnalysisService(
                pdfTextExtractor,
                personMatcher,
                documentClassifier,
                employeeRepository,
                documentCategoryRepository,
                documentAnalysisRepository);
    }

    @Test
    void persistsMatchedAnalysisWithDeterministicScore() {
        var documentId = UUID.randomUUID();
        var employeeId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();
        when(pdfTextExtractor.extract(any())).thenReturn(PdfExtractionResult.success("Anna Müller"));
        when(employeeRepository.findAll()).thenReturn(List.of());
        when(documentCategoryRepository.findAll()).thenReturn(List.of());
        when(personMatcher.match(any(), any()))
                .thenReturn(new PersonMatchResult(
                        MatchStatus.MATCHED, employeeId, List.of(employeeId), "Name im Dokument gefunden"));
        when(documentClassifier.classify(any(), any()))
                .thenReturn(new ClassificationResult(
                        ClassificationAction.USE_EXISTING,
                        categoryId,
                        null,
                        new BigDecimal("0.90"),
                        "Schlüsselwort erkannt: 'vertrag'",
                        "Dokument passt zu bestehender Kategorie 'Verträge'."));
        when(documentAnalysisRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var analysis = service().analyze(documentId, new ByteArrayInputStream(new byte[0]));

        assertThat(analysis.getMatchStatus()).isEqualTo(MatchStatus.MATCHED);
        assertThat(analysis.getMatchedEmployeeId()).isEqualTo(employeeId);
        assertThat(analysis.getMatchScore()).isEqualByComparingTo(new BigDecimal("1.0000"));
        assertThat(analysis.getReviewStatus()).isEqualTo(ReviewStatus.PENDING);
        assertThat(analysis.getSuggestedCategoryId()).isEqualTo(categoryId);
        assertThat(analysis.getCategoryConfidence()).isEqualByComparingTo(new BigDecimal("0.90"));
    }

    @Test
    void persistsSuggestedNewCategoryNameWhenClassifierProposesOne() {
        var documentId = UUID.randomUUID();
        when(pdfTextExtractor.extract(any())).thenReturn(PdfExtractionResult.success("Kündigung"));
        when(employeeRepository.findAll()).thenReturn(List.of());
        when(documentCategoryRepository.findAll()).thenReturn(List.of());
        when(personMatcher.match(any(), any()))
                .thenReturn(new PersonMatchResult(
                        MatchStatus.NO_MATCH, null, List.of(), "Kein Mitarbeitername im Dokument gefunden."));
        when(documentClassifier.classify(any(), any()))
                .thenReturn(new ClassificationResult(
                        ClassificationAction.SUGGEST_NEW,
                        null,
                        "Kündigungen",
                        new BigDecimal("0.60"),
                        "Schlüsselwort erkannt: 'kündigung'",
                        "Neue Kategorie 'Kündigungen' vorgeschlagen."));
        when(documentAnalysisRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var analysis = service().analyze(documentId, new ByteArrayInputStream(new byte[0]));

        assertThat(analysis.getSuggestedCategoryId()).isNull();
        assertThat(analysis.getSuggestedCategoryName()).isEqualTo("Kündigungen");
        assertThat(analysis.getCategoryConfidence()).isEqualByComparingTo(new BigDecimal("0.60"));
    }

    @Test
    void persistsUnreadableDocumentAsNoMatchWithExplanatoryEvidenceAndSkipsClassification() {
        var documentId = UUID.randomUUID();
        when(pdfTextExtractor.extract(any())).thenReturn(PdfExtractionResult.of(PdfExtractionStatus.ENCRYPTED));
        when(documentAnalysisRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var analysis = service().analyze(documentId, new ByteArrayInputStream(new byte[0]));

        assertThat(analysis.getMatchStatus()).isEqualTo(MatchStatus.NO_MATCH);
        assertThat(analysis.getMatchedEmployeeId()).isNull();
        assertThat(analysis.getEvidence()).contains("passwortgeschützt");
        assertThat(analysis.getReviewStatus()).isEqualTo(ReviewStatus.PENDING);
        assertThat(analysis.getSuggestedCategoryId()).isNull();
        assertThat(analysis.getSuggestedCategoryName()).isNull();
    }
}
