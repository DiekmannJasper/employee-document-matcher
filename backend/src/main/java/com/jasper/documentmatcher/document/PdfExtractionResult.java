package com.jasper.documentmatcher.document;

public record PdfExtractionResult(PdfExtractionStatus status, String text) {

    static PdfExtractionResult success(String text) {
        return new PdfExtractionResult(PdfExtractionStatus.SUCCESS, text);
    }

    static PdfExtractionResult of(PdfExtractionStatus status) {
        return new PdfExtractionResult(status, null);
    }
}
