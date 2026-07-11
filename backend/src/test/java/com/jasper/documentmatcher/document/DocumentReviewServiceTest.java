package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.jasper.documentmatcher.employee.EmployeeNotFoundException;
import com.jasper.documentmatcher.employee.EmployeeResponse;
import com.jasper.documentmatcher.employee.EmployeeService;
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
    @Mock private DocumentRepository documentRepository;
    @Mock private DocumentAnalysisRepository documentAnalysisRepository;

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
                "Kein Mitarbeitername im Dokument gefunden.",
                ReviewStatus.PENDING,
                Instant.now());
        when(documentAnalysisRepository.findByReviewStatus(ReviewStatus.PENDING)).thenReturn(List.of(analysis));
        when(documentRepository.findById(document.getId())).thenReturn(Optional.of(document));

        var service = new DocumentReviewService(employeeService, documentRepository, documentAnalysisRepository);
        var result = service.findPendingReviews();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).originalFilename()).isEqualTo("vertrag.pdf");
        assertThat(result.get(0).matchStatus()).isEqualTo(MatchStatus.NO_MATCH);
    }

    @Test
    void confirmingAssignsTheDocumentAndMarksTheAnalysisConfirmed() {
        var employeeId = UUID.randomUUID();
        var document = new Document(
                UUID.randomUUID(), null, "vertrag.pdf", "storage-key", DocumentStatus.UPLOADED, Instant.now());
        var analysis = new DocumentAnalysis(
                UUID.randomUUID(),
                document.getId(),
                MatchStatus.MATCHED,
                employeeId,
                null,
                null,
                null,
                "Name im Dokument gefunden",
                ReviewStatus.PENDING,
                Instant.now());
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentRepository.findById(document.getId())).thenReturn(Optional.of(document));
        when(documentAnalysisRepository.findByDocumentId(document.getId())).thenReturn(Optional.of(analysis));

        var service = new DocumentReviewService(employeeService, documentRepository, documentAnalysisRepository);
        var result = service.confirm(document.getId(), new ConfirmMatchRequest(employeeId));

        assertThat(document.getEmployeeId()).isEqualTo(employeeId);
        assertThat(document.getStatus()).isEqualTo(DocumentStatus.ASSIGNED);
        assertThat(analysis.getReviewStatus()).isEqualTo(ReviewStatus.CONFIRMED);
        assertThat(result.categoryId()).isNull();
    }

    @Test
    void rejectsConfirmationWithoutAnEmployeeId() {
        var service = new DocumentReviewService(employeeService, documentRepository, documentAnalysisRepository);

        assertThatThrownBy(() -> service.confirm(UUID.randomUUID(), new ConfirmMatchRequest(null)))
                .isInstanceOf(InvalidReviewRequestException.class);
    }

    @Test
    void propagatesNotFoundForUnknownEmployee() {
        var employeeId = UUID.randomUUID();
        when(employeeService.findById(employeeId)).thenThrow(new EmployeeNotFoundException(employeeId));

        var service = new DocumentReviewService(employeeService, documentRepository, documentAnalysisRepository);

        assertThatThrownBy(() -> service.confirm(UUID.randomUUID(), new ConfirmMatchRequest(employeeId)))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void rejectsConfirmationForUnknownDocument() {
        var employeeId = UUID.randomUUID();
        var documentId = UUID.randomUUID();
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        var service = new DocumentReviewService(employeeService, documentRepository, documentAnalysisRepository);

        assertThatThrownBy(() -> service.confirm(documentId, new ConfirmMatchRequest(employeeId)))
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
                "Name im Dokument gefunden",
                ReviewStatus.CONFIRMED,
                Instant.now());
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentRepository.findById(document.getId())).thenReturn(Optional.of(document));
        when(documentAnalysisRepository.findByDocumentId(document.getId())).thenReturn(Optional.of(analysis));

        var service = new DocumentReviewService(employeeService, documentRepository, documentAnalysisRepository);

        assertThatThrownBy(() -> service.confirm(document.getId(), new ConfirmMatchRequest(employeeId)))
                .isInstanceOf(DocumentAlreadyReviewedException.class);
    }
}
