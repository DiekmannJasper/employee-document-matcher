package com.jasper.documentmatcher.document;

import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PdfTextExtractor implements DocumentTextExtractor {

    private static final Logger log = LoggerFactory.getLogger(PdfTextExtractor.class);

    @Override
    public boolean supports(DocumentFormat format) {
        return format == DocumentFormat.PDF;
    }

    @Override
    public DocumentExtractionResult extract(InputStream content) {
        byte[] bytes;
        try {
            bytes = content.readAllBytes();
        } catch (IOException e) {
            log.warn("PDF text extraction failed: could not read input stream");
            return DocumentExtractionResult.of(DocumentExtractionStatus.CORRUPTED);
        }

        try (var document = Loader.loadPDF(bytes)) {
            var text = new PDFTextStripper().getText(document);
            var status = text.isBlank() ? DocumentExtractionStatus.EMPTY : DocumentExtractionStatus.SUCCESS;
            log.debug("PDF text extraction completed with status {}", status);
            return status == DocumentExtractionStatus.SUCCESS
                    ? DocumentExtractionResult.success(text)
                    : DocumentExtractionResult.of(status);
        } catch (InvalidPasswordException e) {
            log.debug("PDF text extraction completed with status {}", DocumentExtractionStatus.ENCRYPTED);
            return DocumentExtractionResult.of(DocumentExtractionStatus.ENCRYPTED);
        } catch (IOException e) {
            log.debug("PDF text extraction completed with status {}", DocumentExtractionStatus.CORRUPTED);
            return DocumentExtractionResult.of(DocumentExtractionStatus.CORRUPTED);
        }
    }
}
