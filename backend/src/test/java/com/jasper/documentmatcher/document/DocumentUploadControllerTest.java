package com.jasper.documentmatcher.document;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jasper.documentmatcher.common.GlobalExceptionHandler;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DocumentUploadController.class)
@Import(GlobalExceptionHandler.class)
class DocumentUploadControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private DocumentUploadService documentUploadService;

    @Test
    void uploadReturnsCreatedForValidPdf() throws Exception {
        var file = new MockMultipartFile("file", "vertrag.pdf", "application/pdf", "%PDF-1.4".getBytes());
        when(documentUploadService.upload(any()))
                .thenReturn(new DocumentUploadResponse(
                        UUID.randomUUID(), "vertrag.pdf", DocumentStatus.UPLOADED, Instant.now(), null, null));

        mockMvc.perform(multipart("/api/documents").file(file)).andExpect(status().isCreated());
    }

    @Test
    void uploadReturnsBadRequestForInvalidFile() throws Exception {
        var file = new MockMultipartFile("file", "vertrag.txt", "text/plain", "not a pdf".getBytes());
        when(documentUploadService.upload(any()))
                .thenThrow(new InvalidUploadException("Nur PDF-Dateien werden unterstützt."));

        mockMvc.perform(multipart("/api/documents").file(file)).andExpect(status().isBadRequest());
    }

    @Test
    void uploadReturnsBadRequestWhenFileIsMissing() throws Exception {
        mockMvc.perform(multipart("/api/documents")).andExpect(status().isBadRequest());
    }
}
