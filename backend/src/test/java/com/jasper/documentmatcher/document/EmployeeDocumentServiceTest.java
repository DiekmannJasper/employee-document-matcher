package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.jasper.documentmatcher.employee.EmployeeNotFoundException;
import com.jasper.documentmatcher.employee.EmployeeResponse;
import com.jasper.documentmatcher.employee.EmployeeService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeDocumentServiceTest {

    @Mock private EmployeeService employeeService;
    @Mock private DocumentRepository documentRepository;
    @Mock private DocumentAnalysisRepository documentAnalysisRepository;

    @Test
    void returnsDocumentsWithSuggestedCategoryFromAnalysis() {
        var employeeId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();
        var document = new Document(
                UUID.randomUUID(), employeeId, "vertrag.pdf", "storage-key", DocumentStatus.ASSIGNED, Instant.now());
        var analysis = new DocumentAnalysis(
                UUID.randomUUID(),
                document.getId(),
                MatchStatus.MATCHED,
                employeeId,
                null,
                categoryId,
                null,
                "Name gefunden",
                ReviewStatus.CONFIRMED,
                Instant.now());
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentRepository.findByEmployeeId(employeeId)).thenReturn(List.of(document));
        when(documentAnalysisRepository.findByDocumentIdIn(any())).thenReturn(List.of(analysis));

        var service = new EmployeeDocumentService(employeeService, documentRepository, documentAnalysisRepository);
        var result = service.findByEmployee(employeeId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).categoryId()).isEqualTo(categoryId);
        assertThat(result.get(0).originalFilename()).isEqualTo("vertrag.pdf");
    }

    @Test
    void returnsEmptyListWhenEmployeeHasNoDocuments() {
        var employeeId = UUID.randomUUID();
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentRepository.findByEmployeeId(employeeId)).thenReturn(List.of());
        when(documentAnalysisRepository.findByDocumentIdIn(any())).thenReturn(List.of());

        var service = new EmployeeDocumentService(employeeService, documentRepository, documentAnalysisRepository);

        assertThat(service.findByEmployee(employeeId)).isEmpty();
    }

    @Test
    void propagatesNotFoundForUnknownEmployee() {
        var employeeId = UUID.randomUUID();
        when(employeeService.findById(employeeId)).thenThrow(new EmployeeNotFoundException(employeeId));

        var service = new EmployeeDocumentService(employeeService, documentRepository, documentAnalysisRepository);

        assertThatThrownBy(() -> service.findByEmployee(employeeId)).isInstanceOf(EmployeeNotFoundException.class);
    }
}
