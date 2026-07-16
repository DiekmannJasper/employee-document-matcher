package com.jasper.documentmatcher.document;

import java.io.InputStream;

interface DocumentTextExtractor {

    boolean supports(DocumentFormat format);

    DocumentExtractionResult extract(InputStream content);
}
