package com.jasper.documentmatcher.document;

import java.util.UUID;

public class DocumentAlreadyReviewedException extends RuntimeException {

    public DocumentAlreadyReviewedException(UUID documentId) {
        super("Dieses Dokument wurde bereits geprüft: " + documentId);
    }
}
