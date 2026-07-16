package com.jasper.documentmatcher.document;

import java.util.List;

public record ExternalDocumentImportResponse(List<DocumentUploadResponse> documents) {
}
