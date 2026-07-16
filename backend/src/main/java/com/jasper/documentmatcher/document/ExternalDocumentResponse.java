package com.jasper.documentmatcher.document;

public record ExternalDocumentResponse(
        String id, String sourceSystem, String filename, String description, String expectedOutcome) {
}
