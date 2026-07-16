package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jasper.documentmatcher.category.DocumentCategory;
import com.jasper.documentmatcher.category.DocumentCategoryRepository;
import com.jasper.documentmatcher.employee.Employee;
import com.jasper.documentmatcher.employee.EmployeeRepository;
import com.jasper.documentmatcher.storage.DocumentStorage;
import com.jasper.documentmatcher.storage.StorageException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class DocumentUploadServiceTest {

    @Mock private DocumentUploadValidator validator;
    @Mock private DocumentStorage documentStorage;
    @Mock private DocumentRepository documentRepository;
    @Mock private DocumentAnalysisService documentAnalysisService;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private DocumentCategoryRepository documentCategoryRepository;

    @Test
    void storesValidatedFileTriggersAnalysisAndPersistsDocumentMetadata() {
        var file = new MockMultipartFile("file", "vertrag.pdf", "application/pdf", "%PDF-1.4".getBytes());
        when(documentStorage.store(any(), any())).thenReturn("generated-key.pdf");
        when(documentAnalysisService.analyze(any(), any(), any()))
                .thenReturn(new DocumentAnalysis(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        MatchStatus.NO_MATCH,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "Kein Mitarbeitername im Dokument gefunden.",
                        ReviewStatus.PENDING,
                        Instant.now()));

        var service =
                new DocumentUploadService(
                        validator,
                        documentStorage,
                        documentRepository,
                        documentAnalysisService,
                        employeeRepository,
                        documentCategoryRepository);
        var response = service.upload(file);

        verify(validator).validate(file);
        verify(documentRepository).save(any());
        verify(documentAnalysisService).analyze(any(), any(), any());
        assertThat(response.originalFilename()).isEqualTo("vertrag.pdf");
        assertThat(response.status()).isEqualTo(DocumentStatus.UPLOADED);
        assertThat(response.assignedEmployeeName()).isNull();
    }

    @Test
    void autoAssignsDocumentWhenAnalysisHasExactlyOneEmployeeMatch() {
        var file = new MockMultipartFile("file", "vertrag.pdf", "application/pdf", "%PDF-1.4".getBytes());
        var employeeId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();
        var employee = mock(Employee.class);
        var category = mock(DocumentCategory.class);
        when(employee.getFirstName()).thenReturn("Anna");
        when(employee.getLastName()).thenReturn("Müller");
        when(category.getDisplayName()).thenReturn("Verträge");
        when(documentStorage.store(any(), any())).thenReturn("generated-key.pdf");
        when(documentAnalysisService.analyze(any(), any(), any()))
                .thenReturn(new DocumentAnalysis(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        MatchStatus.MATCHED,
                        employeeId,
                        null,
                        categoryId,
                        null,
                        null,
                        null,
                        "Name im Dokument gefunden: 'Anna Müller'",
                        ReviewStatus.PENDING,
                        Instant.now()));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(documentCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        var service =
                new DocumentUploadService(
                        validator,
                        documentStorage,
                        documentRepository,
                        documentAnalysisService,
                        employeeRepository,
                        documentCategoryRepository);

        var response = service.upload(file);

        assertThat(response.status()).isEqualTo(DocumentStatus.ASSIGNED);
        assertThat(response.assignedEmployeeName()).isEqualTo("Anna Müller");
        assertThat(response.assignedCategoryName()).isEqualTo("Verträge");
    }

    @Test
    void deletesTheStoredFileAndPropagatesWhenAnalysisFails() {
        var file = new MockMultipartFile("file", "vertrag.pdf", "application/pdf", "%PDF-1.4".getBytes());
        when(documentStorage.store(any(), any())).thenReturn("generated-key.pdf");
        when(documentAnalysisService.analyze(any(), any(), any())).thenThrow(new IllegalStateException("analysis boom"));

        var service =
                new DocumentUploadService(
                        validator,
                        documentStorage,
                        documentRepository,
                        documentAnalysisService,
                        employeeRepository,
                        documentCategoryRepository);

        assertThatThrownBy(() -> service.upload(file)).isInstanceOf(IllegalStateException.class);
        verify(documentStorage).delete("generated-key.pdf");
    }

    @Test
    void propagatesTheOriginalFailureEvenWhenCleanupItselfFails() {
        var file = new MockMultipartFile("file", "vertrag.pdf", "application/pdf", "%PDF-1.4".getBytes());
        when(documentStorage.store(any(), any())).thenReturn("generated-key.pdf");
        when(documentAnalysisService.analyze(any(), any(), any())).thenThrow(new IllegalStateException("analysis boom"));
        doThrow(new StorageException("cleanup boom")).when(documentStorage).delete("generated-key.pdf");

        var service =
                new DocumentUploadService(
                        validator,
                        documentStorage,
                        documentRepository,
                        documentAnalysisService,
                        employeeRepository,
                        documentCategoryRepository);

        assertThatThrownBy(() -> service.upload(file))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("analysis boom");
    }
}
