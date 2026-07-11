package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;

class PdfTextExtractorTest {

    private final PdfTextExtractor extractor = new PdfTextExtractor();

    @Test
    void extractsTextFromTextBasedPdf() throws IOException {
        var pdfBytes = createPdfWithText("Arbeitsvertrag für Anna Müller");

        var result = extractor.extract(new ByteArrayInputStream(pdfBytes));

        assertThat(result.status()).isEqualTo(PdfExtractionStatus.SUCCESS);
        assertThat(result.text()).contains("Anna Müller");
    }

    @Test
    void reportsEmptyForPdfWithoutText() throws IOException {
        var pdfBytes = createPdfWithText(null);

        var result = extractor.extract(new ByteArrayInputStream(pdfBytes));

        assertThat(result.status()).isEqualTo(PdfExtractionStatus.EMPTY);
        assertThat(result.text()).isNull();
    }

    @Test
    void reportsCorruptedForUnreadableBytes() {
        var garbage = "%PDF-1.4\nthis is not a valid pdf structure".getBytes();

        var result = extractor.extract(new ByteArrayInputStream(garbage));

        assertThat(result.status()).isEqualTo(PdfExtractionStatus.CORRUPTED);
        assertThat(result.text()).isNull();
    }

    @Test
    void reportsEncryptedForPasswordProtectedPdf() throws Exception {
        var pdfBytes = createPasswordProtectedPdf();

        var result = extractor.extract(new ByteArrayInputStream(pdfBytes));

        assertThat(result.status()).isEqualTo(PdfExtractionStatus.ENCRYPTED);
        assertThat(result.text()).isNull();
    }

    private byte[] createPdfWithText(String text) throws IOException {
        try (var document = new PDDocument()) {
            var page = new PDPage();
            document.addPage(page);

            if (text != null) {
                try (var contentStream = new PDPageContentStream(document, page)) {
                    contentStream.beginText();
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                    contentStream.newLineAtOffset(50, 700);
                    contentStream.showText(text);
                    contentStream.endText();
                }
            }

            var output = new ByteArrayOutputStream();
            document.save(output);
            return output.toByteArray();
        }
    }

    private byte[] createPasswordProtectedPdf() throws Exception {
        try (var document = new PDDocument()) {
            document.addPage(new PDPage());

            var protectionPolicy = new StandardProtectionPolicy("owner-secret", "user-secret", new AccessPermission());
            protectionPolicy.setEncryptionKeyLength(128);
            document.protect(protectionPolicy);

            var output = new ByteArrayOutputStream();
            document.save(output);
            return output.toByteArray();
        }
    }
}
