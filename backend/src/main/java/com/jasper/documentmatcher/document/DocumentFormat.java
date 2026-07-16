package com.jasper.documentmatcher.document;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

/**
 * The document formats accepted for manual upload and external import. Detection is based on
 * magic bytes rather than the client-supplied content-type, since browsers are inconsistent about
 * what content-type they send for less common formats such as .docx or .xml.
 */
public enum DocumentFormat {
    PDF("application/pdf", "pdf", "%PDF-".getBytes(StandardCharsets.US_ASCII)),
    DOCX(
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "docx",
            new byte[] {0x50, 0x4B, 0x03, 0x04}),
    XML("application/xml", "xml", "<?xml".getBytes(StandardCharsets.US_ASCII));

    private final String contentType;
    private final String extension;
    private final byte[] signature;

    DocumentFormat(String contentType, String extension, byte[] signature) {
        this.contentType = contentType;
        this.extension = extension;
        this.signature = signature;
    }

    public String contentType() {
        return contentType;
    }

    public String extension() {
        return extension;
    }

    static int maxSignatureLength() {
        return Arrays.stream(values()).mapToInt(format -> format.signature.length).max().orElse(0);
    }

    static Optional<DocumentFormat> detect(byte[] header) {
        return Arrays.stream(values()).filter(format -> format.matchesSignature(header)).findFirst();
    }

    private boolean matchesSignature(byte[] header) {
        return header.length >= signature.length
                && Arrays.equals(header, 0, signature.length, signature, 0, signature.length);
    }
}
