package com.jasper.documentmatcher.document;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jasper.documentmatcher.common.GlobalExceptionHandler;
import com.jasper.documentmatcher.employee.EmployeeNotFoundException;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeDocumentController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeDocumentControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private EmployeeDocumentService employeeDocumentService;

    @Test
    void listReturnsDocumentsForEmployee() throws Exception {
        var employeeId = UUID.randomUUID();
        var response = new DocumentSummaryResponse(UUID.randomUUID(), "vertrag.pdf", null, "application/pdf", Instant.now());
        when(employeeDocumentService.findByEmployee(employeeId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/employees/{employeeId}/documents", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].originalFilename").value("vertrag.pdf"));
    }

    @Test
    void listReturnsNotFoundForUnknownEmployee() throws Exception {
        var employeeId = UUID.randomUUID();
        when(employeeDocumentService.findByEmployee(employeeId)).thenThrow(new EmployeeNotFoundException(employeeId));

        mockMvc.perform(get("/api/employees/{employeeId}/documents", employeeId)).andExpect(status().isNotFound());
    }

    @Test
    void listReturnsBadRequestForInvalidEmployeeId() throws Exception {
        mockMvc.perform(get("/api/employees/{employeeId}/documents", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void openDocumentReturnsPdfInline() throws Exception {
        var employeeId = UUID.randomUUID();
        var documentId = UUID.randomUUID();
        when(employeeDocumentService.openDocument(employeeId, documentId))
                .thenReturn(new DocumentFileResponse("vertrag.pdf", "application/pdf", new ByteArrayInputStream("pdf".getBytes())));

        mockMvc.perform(get("/api/employees/{employeeId}/documents/{documentId}/file", employeeId, documentId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"vertrag.pdf\""));
    }

    @Test
    void openDocumentReturnsNotFoundWhenDocumentDoesNotBelongToEmployee() throws Exception {
        var employeeId = UUID.randomUUID();
        var documentId = UUID.randomUUID();
        when(employeeDocumentService.openDocument(employeeId, documentId)).thenThrow(new DocumentNotFoundException(documentId));

        mockMvc.perform(get("/api/employees/{employeeId}/documents/{documentId}/file", employeeId, documentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCategoryReturnsTheUpdatedDocument() throws Exception {
        var employeeId = UUID.randomUUID();
        var documentId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();
        when(employeeDocumentService.updateCategory(eq(employeeId), eq(documentId), eq(categoryId)))
                .thenReturn(new DocumentSummaryResponse(documentId, "vertrag.pdf", categoryId, "application/pdf", Instant.now()));

        mockMvc.perform(patch("/api/employees/{employeeId}/documents/{documentId}/category", employeeId, documentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryId\":\"" + categoryId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(categoryId.toString()));
    }
}
