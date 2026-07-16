package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jasper.documentmatcher.category.DocumentCategoryService;
import com.jasper.documentmatcher.employee.EmployeeNotFoundException;
import com.jasper.documentmatcher.employee.EmployeeResponse;
import com.jasper.documentmatcher.employee.EmployeeService;
import com.jasper.documentmatcher.storage.DocumentStorage;
import java.io.ByteArrayInputStream;
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
    @Mock private DocumentCategoryService documentCategoryService;
    @Mock private DocumentRepository documentRepository;
    @Mock private DocumentStorage documentStorage;

    @Test
    void returnsDocumentsWithTheirConfirmedCategory() {
        var employeeId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();
        var document = new Document(
                UUID.randomUUID(), employeeId, "vertrag.pdf", "storage-key", "application/pdf", DocumentStatus.ASSIGNED, Instant.now());
        document.assignCategory(categoryId);
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentRepository.findByEmployeeId(employeeId)).thenReturn(List.of(document));

        var service = new EmployeeDocumentService(employeeService, documentCategoryService, documentRepository, documentStorage);
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

        var service = new EmployeeDocumentService(employeeService, documentCategoryService, documentRepository, documentStorage);

        assertThat(service.findByEmployee(employeeId)).isEmpty();
    }

    @Test
    void propagatesNotFoundForUnknownEmployee() {
        var employeeId = UUID.randomUUID();
        when(employeeService.findById(employeeId)).thenThrow(new EmployeeNotFoundException(employeeId));

        var service = new EmployeeDocumentService(employeeService, documentCategoryService, documentRepository, documentStorage);

        assertThatThrownBy(() -> service.findByEmployee(employeeId)).isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void opensDocumentWhenItBelongsToEmployee() throws Exception {
        var employeeId = UUID.randomUUID();
        var documentId = UUID.randomUUID();
        var document = new Document(documentId, employeeId, "vertrag.pdf", "storage-key", "application/pdf", DocumentStatus.ASSIGNED, Instant.now());
        var content = new ByteArrayInputStream("pdf".getBytes());
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentRepository.findById(documentId)).thenReturn(java.util.Optional.of(document));
        when(documentStorage.load("storage-key")).thenReturn(content);

        var service = new EmployeeDocumentService(employeeService, documentCategoryService, documentRepository, documentStorage);

        var result = service.openDocument(employeeId, documentId);

        assertThat(result.filename()).isEqualTo("vertrag.pdf");
        assertThat(result.content().readAllBytes()).isEqualTo("pdf".getBytes());
    }

    @Test
    void refusesDocumentFromAnotherEmployee() {
        var employeeId = UUID.randomUUID();
        var otherEmployeeId = UUID.randomUUID();
        var documentId = UUID.randomUUID();
        var document = new Document(
                documentId, otherEmployeeId, "vertrag.pdf", "storage-key", "application/pdf", DocumentStatus.ASSIGNED, Instant.now());
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentRepository.findById(documentId)).thenReturn(java.util.Optional.of(document));

        var service = new EmployeeDocumentService(employeeService, documentCategoryService, documentRepository, documentStorage);

        assertThatThrownBy(() -> service.openDocument(employeeId, documentId)).isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    void updatesTheCategoryOfAnAssignedEmployeeDocument() {
        var employeeId = UUID.randomUUID();
        var documentId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();
        var document = new Document(documentId, employeeId, "vertrag.pdf", "storage-key", "application/pdf", DocumentStatus.ASSIGNED, Instant.now());
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentRepository.findById(documentId)).thenReturn(java.util.Optional.of(document));

        var service = new EmployeeDocumentService(employeeService, documentCategoryService, documentRepository, documentStorage);
        var result = service.updateCategory(employeeId, documentId, categoryId);

        verify(documentCategoryService).findById(categoryId);
        assertThat(result.categoryId()).isEqualTo(categoryId);
    }

    @Test
    void movesTheDocumentBackToUnassignedCategory() {
        var employeeId = UUID.randomUUID();
        var documentId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();
        var document = new Document(documentId, employeeId, "vertrag.pdf", "storage-key", "application/pdf", DocumentStatus.ASSIGNED, Instant.now());
        document.assignCategory(categoryId);
        when(employeeService.findById(employeeId))
                .thenReturn(new EmployeeResponse(employeeId, "EMP-1001", "Anna", "Müller", "IT"));
        when(documentRepository.findById(documentId)).thenReturn(java.util.Optional.of(document));

        var service = new EmployeeDocumentService(employeeService, documentCategoryService, documentRepository, documentStorage);
        var result = service.updateCategory(employeeId, documentId, null);

        assertThat(result.categoryId()).isNull();
    }
}
