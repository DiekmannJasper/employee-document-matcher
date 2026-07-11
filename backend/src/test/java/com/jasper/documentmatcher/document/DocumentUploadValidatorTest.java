package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class DocumentUploadValidatorTest {

    private final DocumentUploadValidator validator = new DocumentUploadValidator();

    @Test
    void acceptsValidPdf() {
        var file = new MockMultipartFile(
                "file", "vertrag.pdf", "application/pdf", "%PDF-1.4 rest of file".getBytes());

        assertThatCode(() -> validator.validate(file)).doesNotThrowAnyException();
    }

    @Test
    void rejectsEmptyFile() {
        var file = new MockMultipartFile("file", "leer.pdf", "application/pdf", new byte[0]);

        assertThatThrownBy(() -> validator.validate(file)).isInstanceOf(InvalidUploadException.class);
    }

    @Test
    void rejectsMissingFile() {
        assertThatThrownBy(() -> validator.validate(null)).isInstanceOf(InvalidUploadException.class);
    }

    @Test
    void rejectsWrongContentType() {
        var file = new MockMultipartFile("file", "vertrag.txt", "text/plain", "%PDF-1.4".getBytes());

        assertThatThrownBy(() -> validator.validate(file)).isInstanceOf(InvalidUploadException.class);
    }

    @Test
    void rejectsFileWithoutPdfSignature() {
        var file = new MockMultipartFile(
                "file", "vertrag.pdf", "application/pdf", "not really a pdf".getBytes());

        assertThatThrownBy(() -> validator.validate(file)).isInstanceOf(InvalidUploadException.class);
    }
}
