package com.jasper.documentmatcher.document;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Format is detected from magic bytes, not the client-supplied content-type - browsers are
 * inconsistent about what content-type they send for .docx/.xml, and a spoofed content-type
 * header would not have added any real security anyway.
 */
@Component
class DocumentUploadValidator {

    void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidUploadException("Es wird genau eine Datei erwartet.");
        }

        if (detectFormat(file).isEmpty()) {
            throw new InvalidUploadException("Nur PDF-, Word- (.docx) und XML-Dateien werden unterstützt.");
        }
    }

    private Optional<DocumentFormat> detectFormat(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            var header = inputStream.readNBytes(DocumentFormat.maxSignatureLength());
            return DocumentFormat.detect(header);
        } catch (IOException e) {
            throw new InvalidUploadException("Die Datei konnte nicht gelesen werden.");
        }
    }
}
