package com.jasper.documentmatcher.document;

import java.util.List;

interface ExternalDocumentProvider {

    List<ExternalDocumentResponse> findAvailableDocuments();

    ExternalDocumentContent fetch(String externalDocumentId);

    default List<ExternalDocumentContent> fetchAll(String externalDocumentId) {
        return List.of(fetch(externalDocumentId));
    }
}
