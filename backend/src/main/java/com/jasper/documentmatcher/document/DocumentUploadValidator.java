package com.jasper.documentmatcher.document;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
class DocumentUploadValidator {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final byte[] PDF_SIGNATURE = "%PDF-".getBytes(StandardCharsets.US_ASCII);

    void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidUploadException("Es wird genau eine PDF-Datei erwartet.");
        }

        if (!PDF_CONTENT_TYPE.equals(file.getContentType())) {
            throw new InvalidUploadException("Nur PDF-Dateien werden unterstützt.");
        }

        if (!hasPdfSignature(file)) {
            throw new InvalidUploadException("Die Datei ist keine gültige PDF-Datei.");
        }
    }

    private boolean hasPdfSignature(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            var header = inputStream.readNBytes(PDF_SIGNATURE.length);
            return Arrays.equals(header, PDF_SIGNATURE);
        } catch (IOException e) {
            throw new InvalidUploadException("Die Datei konnte nicht gelesen werden.");
        }
    }
}
