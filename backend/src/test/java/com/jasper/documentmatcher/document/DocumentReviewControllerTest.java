package com.jasper.documentmatcher.document;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasper.documentmatcher.common.GlobalExceptionHandler;
import com.jasper.documentmatcher.confidence.ConfidenceLevel;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DocumentReviewController.class)
@Import(GlobalExceptionHandler.class)
class DocumentReviewControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private DocumentReviewService documentReviewService;

    @Test
    void listsPendingReviews() throws Exception {
        var response = new PendingReviewResponse(
                UUID.randomUUID(),
                "vertrag.pdf",
                MatchStatus.NO_MATCH,
                null,
                "Kein Treffer",
                ConfidenceLevel.NONE,
                null,
                null,
                ConfidenceLevel.NONE,
                Instant.now());
        when(documentReviewService.findPendingReviews()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/documents/pending-review"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].originalFilename").value("vertrag.pdf"));
    }

    @Test
    void confirmsAMatch() throws Exception {
        var documentId = UUID.randomUUID();
        var employeeId = UUID.randomUUID();
        var response =
                new DocumentSummaryResponse(documentId, "vertrag.pdf", null, Instant.now());
        when(documentReviewService.confirm(eq(documentId), any())).thenReturn(response);

        mockMvc.perform(post("/api/documents/{documentId}/confirmation", documentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ConfirmMatchRequest(employeeId, null, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalFilename").value("vertrag.pdf"));
    }

    @Test
    void confirmReturnsConflictWhenAlreadyReviewed() throws Exception {
        var documentId = UUID.randomUUID();
        var employeeId = UUID.randomUUID();
        when(documentReviewService.confirm(eq(documentId), any()))
                .thenThrow(new DocumentAlreadyReviewedException(documentId));

        mockMvc.perform(post("/api/documents/{documentId}/confirmation", documentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ConfirmMatchRequest(employeeId, null, null))))
                .andExpect(status().isConflict());
    }

    @Test
    void confirmReturnsNotFoundForUnknownDocument() throws Exception {
        var documentId = UUID.randomUUID();
        var employeeId = UUID.randomUUID();
        when(documentReviewService.confirm(eq(documentId), any())).thenThrow(new DocumentNotFoundException(documentId));

        mockMvc.perform(post("/api/documents/{documentId}/confirmation", documentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ConfirmMatchRequest(employeeId, null, null))))
                .andExpect(status().isNotFound());
    }
}
