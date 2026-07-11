package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
    @Mock private EmployeeRepository employeeRepository;
    @Mock private DocumentAnalysisRepository documentAnalysisRepository;

    @Test
    void persistsMatchedAnalysisWithDeterministicScore() {
        var documentId = UUID.randomUUID();
        var employeeId = UUID.randomUUID();
        when(pdfTextExtractor.extract(any()))
                .thenReturn(PdfExtractionResult.success("Anna Müller"));
        when(employeeRepository.findAll()).thenReturn(List.of());
        when(personMatcher.match(any(), any()))
                .thenReturn(new PersonMatchResult(
                        MatchStatus.MATCHED, employeeId, List.of(employeeId), "Name im Dokument gefunden"));
        when(documentAnalysisRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var service = new DocumentAnalysisService(
                pdfTextExtractor, personMatcher, employeeRepository, documentAnalysisRepository);
        var analysis = service.analyze(documentId, new ByteArrayInputStream(new byte[0]));

        assertThat(analysis.getMatchStatus()).isEqualTo(MatchStatus.MATCHED);
        assertThat(analysis.getMatchedEmployeeId()).isEqualTo(employeeId);
        assertThat(analysis.getMatchScore()).isEqualByComparingTo(new BigDecimal("1.0000"));
        assertThat(analysis.getReviewStatus()).isEqualTo(ReviewStatus.PENDING);
    }

    @Test
    void persistsUnreadableDocumentAsNoMatchWithExplanatoryEvidence() {
        var documentId = UUID.randomUUID();
        when(pdfTextExtractor.extract(any())).thenReturn(PdfExtractionResult.of(PdfExtractionStatus.ENCRYPTED));
        when(documentAnalysisRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var service = new DocumentAnalysisService(
                pdfTextExtractor, personMatcher, employeeRepository, documentAnalysisRepository);
        var analysis = service.analyze(documentId, new ByteArrayInputStream(new byte[0]));

        assertThat(analysis.getMatchStatus()).isEqualTo(MatchStatus.NO_MATCH);
        assertThat(analysis.getMatchedEmployeeId()).isNull();
        assertThat(analysis.getEvidence()).contains("passwortgeschützt");
        assertThat(analysis.getReviewStatus()).isEqualTo(ReviewStatus.PENDING);
    }
}
