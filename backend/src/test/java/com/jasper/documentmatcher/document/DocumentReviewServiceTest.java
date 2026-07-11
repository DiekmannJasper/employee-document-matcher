package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jasper.documentmatcher.category.CategoryNotFoundException;
import com.jasper.documentmatcher.category.CategoryOrigin;
import com.jasper.documentmatcher.category.DocumentCategoryResponse;
import com.jasper.documentmatcher.category.DocumentCategoryService;
import com.jasper.documentmatcher.confidence.ConfidenceBandCalculator;
import com.jasper.documentmatcher.confidence.ConfidenceLevel;
import com.jasper.documentmatcher.employee.EmployeeNotFoundException;
import com.jasper.documentmatcher.employee.EmployeeResponse;
import com.jasper.documentmatcher.employee.EmployeeService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentReviewServiceTest {

    @Mock private EmployeeService employeeService;
    @Mock private DocumentCategoryService documentCategoryService;
    @Mock private DocumentRepository documentRepository;
    @Mock private DocumentAnalysisRepository documentAnalysisRepository;

    private final ConfidenceBandCalculator confidenceBandCalculator =
            new ConfidenceBandCalculator(new BigDecimal("0.80"), new BigDecimal("0.40"));

    private DocumentReviewService service() {
        return new DocumentReviewService(
                employeeService, documentCategoryService, confidenceBandCalculator, documentRepository, documentAnalysisRepository);
    }

    private DocumentAnalysis pendingAnalysis(UUID documentId, UUID matchedEmployeeId, String suggestedCategoryName) {
        return new DocumentAnalysis(
                UUID.randomUUID(),
                documentId,
                MatchStatus.MATCHED,
                matchedEmployeeId,
                null,
                null,
                suggestedCategoryName,
                null,
                "Name im Dokument gefunden",
                ReviewStatus.PENDING,
                Instant.now());
    }

    @Test
    void listsPendingReviewsJoinedWithTheirDocument() {
        var document = new Document(
                UUID.randomUUID(), null, "vertrag.pdf", "storage-key", DocumentStatus.UPLOADED, Instant.now());
        var analysis = new DocumentAnalysis(
                UUID.randomUUID(),
                document.getId(),
                MatchStatus.NO_MATCH,
                null,
                null,
                null,
                null,
                null,
                "Kein Mitarbeitername im Dokument gefunden.",
                ReviewStatus.PENDING,
                Instant.now());
        when(documentAnalysisRepository.findByReviewStatus(ReviewStatus.PENDING)).thenReturn(List.of(analysis));
        when(documentRepository.findById(document.getId())).thenReturn(Optional.of(document));

        var result = service().findPendingReviews();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).originalFilename()).isEqualTo("vertrag.pdf");
        assertThat(result.get(0).matchStatus()).isEqualTo(MatchStatus.NO_MATCH);
        assertThat(result.get(0).systemScore()).isEqualTo(ConfidenceLevel.NONE);
        assertThat(result.get(0).llmConfidence()).isEqualTo(ConfidenceLevel.NONE);
    }

    @Test
    void bandsMatchAndCategoryScoresSeparately() {
        var document = new Document(
                UUID.randomUUID(), null, "vertrag.pdf", "storage-key", DocumentStatus.UPLOADED, Instant.now());
        var analysis = new DocumentAnalysis(
                UUID.randomUUID(),
                document.getId(),
                MatchStatus.MATCHED,
                UUID.randomUUID(),
                new BigDecimal("1.0000"),
                UUID.randomUUID(),
                null,
                new BigDecimal("0.6000"),
                "Name im Dokument gefunden",
                ReviewStatus.PENDING,
                Instant.now());
        when(documentAnalysisRepository.findByReviewStatus(ReviewStatus.PENDING)).thenReturn(List.of(analysis));
        when(documentRepository.findById(document.getId())).thenReturn(Optional.of(document));

        var result = service().findPendingReviews();

        assertThat(result.get(0).systemScore()).isEqualTo(ConfidenceLevel.HIGH);
        assertThat(result.get(0).llmConfidence()).isEqualTo(ConfidenceLevel.MEDIUM);
    }

    @Test
    void confirmingAssignsTheDocumentAndMarksTheAnalysisConfirmed() {
        var employeeId = UUID.randomUUID();
        var document = new Document(
                UUID.randomUUID(), null, "vertrag.pdf", "storage-key", DocumentStatus.UPLOADED, Instant.now());
        var analysis = pendingAnalysis(document.getId(), employeeId, null);
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentRepository.findById(document.getId())).thenReturn(Optional.of(document));
        when(documentAnalysisRepository.findByDocumentId(document.getId())).thenReturn(Optional.of(analysis));

        var result = service().confirm(document.getId(), new ConfirmMatchRequest(employeeId, null, null));

        assertThat(document.getEmployeeId()).isEqualTo(employeeId);
        assertThat(document.getStatus()).isEqualTo(DocumentStatus.ASSIGNED);
        assertThat(document.getCategoryId()).isNull();
        assertThat(analysis.getReviewStatus()).isEqualTo(ReviewStatus.CONFIRMED);
        assertThat(result.categoryId()).isNull();
    }

    @Test
    void confirmingWithAnExistingCategoryIdAssignsIt() {
        var employeeId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();
        var document = new Document(
                UUID.randomUUID(), null, "vertrag.pdf", "storage-key", DocumentStatus.UPLOADED, Instant.now());
        var analysis = pendingAnalysis(document.getId(), employeeId, null);
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentCategoryService.findById(categoryId))
                .thenReturn(new DocumentCategoryResponse(categoryId, "CONTRACT", "Verträge", CategoryOrigin.STANDARD));
        when(documentRepository.findById(document.getId())).thenReturn(Optional.of(document));
        when(documentAnalysisRepository.findByDocumentId(document.getId())).thenReturn(Optional.of(analysis));

        service().confirm(document.getId(), new ConfirmMatchRequest(employeeId, categoryId, null));

        assertThat(document.getCategoryId()).isEqualTo(categoryId);
    }

    @Test
    void confirmingWithAnUnknownCategoryIdPropagatesNotFound() {
        var employeeId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();
        var document = new Document(
                UUID.randomUUID(), null, "vertrag.pdf", "storage-key", DocumentStatus.UPLOADED, Instant.now());
        var analysis = pendingAnalysis(document.getId(), employeeId, null);
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentCategoryService.findById(categoryId)).thenThrow(new CategoryNotFoundException(categoryId));
        when(documentRepository.findById(document.getId())).thenReturn(Optional.of(document));
        when(documentAnalysisRepository.findByDocumentId(document.getId())).thenReturn(Optional.of(analysis));

        assertThatThrownBy(() -> service().confirm(document.getId(), new ConfirmMatchRequest(employeeId, categoryId, null)))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void confirmingWithANewCategoryNameMatchingTheSuggestionUsesLlmSuggestedOrigin() {
        var employeeId = UUID.randomUUID();
        var newCategoryId = UUID.randomUUID();
        var document = new Document(
                UUID.randomUUID(), null, "kuendigung.pdf", "storage-key", DocumentStatus.UPLOADED, Instant.now());
        var analysis = pendingAnalysis(document.getId(), employeeId, "Kündigungen");
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentCategoryService.resolveOrCreateByDisplayName(eq("Kündigungen"), any()))
                .thenReturn(newCategoryId);
        when(documentRepository.findById(document.getId())).thenReturn(Optional.of(document));
        when(documentAnalysisRepository.findByDocumentId(document.getId())).thenReturn(Optional.of(analysis));

        service().confirm(document.getId(), new ConfirmMatchRequest(employeeId, null, "Kündigungen"));

        assertThat(document.getCategoryId()).isEqualTo(newCategoryId);
        verify(documentCategoryService).resolveOrCreateByDisplayName("Kündigungen", CategoryOrigin.LLM_SUGGESTED);
    }

    @Test
    void confirmingWithACustomCategoryNameUsesManualOrigin() {
        var employeeId = UUID.randomUUID();
        var newCategoryId = UUID.randomUUID();
        var document = new Document(
                UUID.randomUUID(), null, "sonstiges.pdf", "storage-key", DocumentStatus.UPLOADED, Instant.now());
        var analysis = pendingAnalysis(document.getId(), employeeId, null);
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentCategoryService.resolveOrCreateByDisplayName(eq("Betriebsvereinbarungen"), any()))
                .thenReturn(newCategoryId);
        when(documentRepository.findById(document.getId())).thenReturn(Optional.of(document));
        when(documentAnalysisRepository.findByDocumentId(document.getId())).thenReturn(Optional.of(analysis));

        service().confirm(document.getId(), new ConfirmMatchRequest(employeeId, null, "Betriebsvereinbarungen"));

        verify(documentCategoryService).resolveOrCreateByDisplayName("Betriebsvereinbarungen", CategoryOrigin.MANUAL);
    }

    @Test
    void rejectsConfirmationWithBothCategoryIdAndNewCategoryName() {
        assertThatThrownBy(() -> service()
                        .confirm(UUID.randomUUID(), new ConfirmMatchRequest(UUID.randomUUID(), UUID.randomUUID(), "Neu")))
                .isInstanceOf(InvalidReviewRequestException.class);
    }

    @Test
    void rejectsConfirmationWithoutAnEmployeeId() {
        assertThatThrownBy(() -> service().confirm(UUID.randomUUID(), new ConfirmMatchRequest(null, null, null)))
                .isInstanceOf(InvalidReviewRequestException.class);
    }

    @Test
    void propagatesNotFoundForUnknownEmployee() {
        var employeeId = UUID.randomUUID();
        when(employeeService.findById(employeeId)).thenThrow(new EmployeeNotFoundException(employeeId));

        assertThatThrownBy(() -> service().confirm(UUID.randomUUID(), new ConfirmMatchRequest(employeeId, null, null)))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void rejectsConfirmationForUnknownDocument() {
        var employeeId = UUID.randomUUID();
        var documentId = UUID.randomUUID();
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().confirm(documentId, new ConfirmMatchRequest(employeeId, null, null)))
                .isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    void rejectsConfirmationForAlreadyReviewedDocument() {
        var employeeId = UUID.randomUUID();
        var document = new Document(
                UUID.randomUUID(), employeeId, "vertrag.pdf", "storage-key", DocumentStatus.ASSIGNED, Instant.now());
        var analysis = new DocumentAnalysis(
                UUID.randomUUID(),
                document.getId(),
                MatchStatus.MATCHED,
                employeeId,
                null,
                null,
                null,
                null,
                "Name im Dokument gefunden",
                ReviewStatus.CONFIRMED,
                Instant.now());
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentRepository.findById(document.getId())).thenReturn(Optional.of(document));
        when(documentAnalysisRepository.findByDocumentId(document.getId())).thenReturn(Optional.of(analysis));

        assertThatThrownBy(
                        () -> service().confirm(document.getId(), new ConfirmMatchRequest(employeeId, null, null)))
                .isInstanceOf(DocumentAlreadyReviewedException.class);
    }
}
