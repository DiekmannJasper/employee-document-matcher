package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jasper.documentmatcher.storage.DocumentStorage;
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

    @Test
    void storesValidatedFileTriggersAnalysisAndPersistsDocumentMetadata() {
        var file = new MockMultipartFile("file", "vertrag.pdf", "application/pdf", "%PDF-1.4".getBytes());
        when(documentStorage.store(any())).thenReturn("generated-key.pdf");

        var service =
                new DocumentUploadService(validator, documentStorage, documentRepository, documentAnalysisService);
        var response = service.upload(file);

        verify(validator).validate(file);
        verify(documentRepository).save(any());
        verify(documentAnalysisService).analyze(any(), any());
        assertThat(response.originalFilename()).isEqualTo("vertrag.pdf");
        assertThat(response.status()).isEqualTo(DocumentStatus.UPLOADED);
    }
}
