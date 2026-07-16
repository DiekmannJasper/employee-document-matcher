package com.jasper.documentmatcher.document;

public class ExternalDocumentNotFoundException extends RuntimeException {

    public ExternalDocumentNotFoundException(String externalDocumentId) {
        super("Externes Dokument nicht gefunden: " + externalDocumentId);
    }
}
