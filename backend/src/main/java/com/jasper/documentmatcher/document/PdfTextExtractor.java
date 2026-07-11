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
public class PdfTextExtractor {

    private static final Logger log = LoggerFactory.getLogger(PdfTextExtractor.class);

    public PdfExtractionResult extract(InputStream pdfContent) {
        byte[] bytes;
        try {
            bytes = pdfContent.readAllBytes();
        } catch (IOException e) {
            log.warn("PDF text extraction failed: could not read input stream");
            return PdfExtractionResult.of(PdfExtractionStatus.CORRUPTED);
        }

        try (var document = Loader.loadPDF(bytes)) {
            var text = new PDFTextStripper().getText(document);
            var status = text.isBlank() ? PdfExtractionStatus.EMPTY : PdfExtractionStatus.SUCCESS;
            log.debug("PDF text extraction completed with status {}", status);
            return status == PdfExtractionStatus.SUCCESS
                    ? PdfExtractionResult.success(text)
                    : PdfExtractionResult.of(status);
        } catch (InvalidPasswordException e) {
            log.debug("PDF text extraction completed with status {}", PdfExtractionStatus.ENCRYPTED);
            return PdfExtractionResult.of(PdfExtractionStatus.ENCRYPTED);
        } catch (IOException e) {
            log.debug("PDF text extraction completed with status {}", PdfExtractionStatus.CORRUPTED);
            return PdfExtractionResult.of(PdfExtractionStatus.CORRUPTED);
        }
    }
}
