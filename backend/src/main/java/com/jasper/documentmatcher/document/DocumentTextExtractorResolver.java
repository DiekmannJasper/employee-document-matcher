package com.jasper.documentmatcher.document;

import java.io.InputStream;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class DocumentTextExtractorResolver {

    private final List<DocumentTextExtractor> extractors;

    DocumentTextExtractorResolver(List<DocumentTextExtractor> extractors) {
        this.extractors = extractors;
    }

    DocumentExtractionResult extract(DocumentFormat format, InputStream content) {
        return extractors.stream()
                .filter(extractor -> extractor.supports(format))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No text extractor registered for format " + format))
                .extract(content);
    }
}
