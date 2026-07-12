package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jasper.documentmatcher.storage.DocumentStorage;
import com.jasper.documentmatcher.storage.StorageException;
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

    @Test
    void deletesTheStoredFileAndPropagatesWhenAnalysisFails() {
        var file = new MockMultipartFile("file", "vertrag.pdf", "application/pdf", "%PDF-1.4".getBytes());
        when(documentStorage.store(any())).thenReturn("generated-key.pdf");
        when(documentAnalysisService.analyze(any(), any())).thenThrow(new IllegalStateException("analysis boom"));

        var service =
                new DocumentUploadService(validator, documentStorage, documentRepository, documentAnalysisService);

        assertThatThrownBy(() -> service.upload(file)).isInstanceOf(IllegalStateException.class);
        verify(documentStorage).delete("generated-key.pdf");
    }

    @Test
    void propagatesTheOriginalFailureEvenWhenCleanupItselfFails() {
        var file = new MockMultipartFile("file", "vertrag.pdf", "application/pdf", "%PDF-1.4".getBytes());
        when(documentStorage.store(any())).thenReturn("generated-key.pdf");
        when(documentAnalysisService.analyze(any(), any())).thenThrow(new IllegalStateException("analysis boom"));
        doThrow(new StorageException("cleanup boom")).when(documentStorage).delete("generated-key.pdf");

        var service =
                new DocumentUploadService(validator, documentStorage, documentRepository, documentAnalysisService);

        assertThatThrownBy(() -> service.upload(file))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("analysis boom");
    }
}
