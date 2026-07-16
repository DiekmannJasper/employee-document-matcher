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
    void acceptsValidDocx() {
        var signature = new byte[] {0x50, 0x4B, 0x03, 0x04, 'r', 'e', 's', 't'};
        var file = new MockMultipartFile(
                "file", "vertrag.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", signature);

        assertThatCode(() -> validator.validate(file)).doesNotThrowAnyException();
    }

    @Test
    void acceptsValidXml() {
        var file = new MockMultipartFile("file", "export.xml", "application/xml", "<?xml version=\"1.0\"?><root/>".getBytes());

        assertThatCode(() -> validator.validate(file)).doesNotThrowAnyException();
    }

    @Test
    void acceptsFileWithValidSignatureRegardlessOfDeclaredContentType() {
        // Format is detected from magic bytes, not the browser-supplied content-type, since that
        // header is trivially spoofable and unreliable for less common formats anyway.
        var file = new MockMultipartFile("file", "vertrag.txt", "text/plain", "%PDF-1.4".getBytes());

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
    void rejectsUnsupportedFileFormat() {
        var file = new MockMultipartFile("file", "vertrag.pdf", "application/pdf", "not really a pdf".getBytes());

        assertThatThrownBy(() -> validator.validate(file)).isInstanceOf(InvalidUploadException.class);
    }
}
