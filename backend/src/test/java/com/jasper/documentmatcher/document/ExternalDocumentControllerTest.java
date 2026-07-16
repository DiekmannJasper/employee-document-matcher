package com.jasper.documentmatcher.document;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jasper.documentmatcher.common.GlobalExceptionHandler;
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

@WebMvcTest(ExternalDocumentController.class)
@Import(GlobalExceptionHandler.class)
class ExternalDocumentControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private ExternalDocumentProvider externalDocumentProvider;
    @MockitoBean private DocumentUploadService documentUploadService;

    @Test
    void listsAvailableExternalDocuments() throws Exception {
        when(externalDocumentProvider.findAvailableDocuments())
                .thenReturn(List.of(new ExternalDocumentResponse(
                        "datev-salary-laura",
                        "DATEV Mock",
                        "datev-gehaltsabrechnung-laura-hoffmann.pdf",
                        "Gehaltsabrechnung aus DATEV",
                        "Auto-Zuordnung")));

        mockMvc.perform(get("/api/external-documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sourceSystem").value("DATEV Mock"));
    }

    @Test
    void importsAnExternalDocumentThroughTheUploadFlow() throws Exception {
        var externalDocument = new ExternalDocumentContent("datev.pdf", "%PDF-1.4".getBytes());
        when(externalDocumentProvider.fetch("datev-salary-laura")).thenReturn(externalDocument);
        when(externalDocumentProvider.fetchAll("datev-salary-laura")).thenReturn(List.of(externalDocument));
        when(documentUploadService.importExternal(externalDocument))
                .thenReturn(new DocumentUploadResponse(
                        UUID.randomUUID(), "datev.pdf", DocumentStatus.UPLOADED, Instant.now(), null, null));

        mockMvc.perform(post("/api/external-documents/imports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"externalDocumentId\":\"datev-salary-laura\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.documents[0].originalFilename").value("datev.pdf"));
    }
}
