package com.jasper.documentmatcher.document;

public record DocumentExtractionResult(DocumentExtractionStatus status, String text) {

    static DocumentExtractionResult success(String text) {
        return new DocumentExtractionResult(DocumentExtractionStatus.SUCCESS, text);
    }

    static DocumentExtractionResult of(DocumentExtractionStatus status) {
        return new DocumentExtractionResult(status, null);
    }
}
