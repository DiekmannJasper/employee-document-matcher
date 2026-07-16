package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class DocumentFormatTest {

    @Test
    void detectsPdfFromSignature() {
        assertThat(DocumentFormat.detect("%PDF-1.4 rest".getBytes(StandardCharsets.US_ASCII)))
                .contains(DocumentFormat.PDF);
    }

    @Test
    void detectsDocxFromZipSignature() {
        var header = new byte[] {0x50, 0x4B, 0x03, 0x04, 0x14, 0x00};

        assertThat(DocumentFormat.detect(header)).contains(DocumentFormat.DOCX);
    }

    @Test
    void detectsXmlFromDeclaration() {
        assertThat(DocumentFormat.detect("<?xml version=\"1.0\"?>".getBytes(StandardCharsets.US_ASCII)))
                .contains(DocumentFormat.XML);
    }

    @Test
    void returnsEmptyForUnknownSignature() {
        assertThat(DocumentFormat.detect("not a supported format".getBytes(StandardCharsets.US_ASCII)))
                .isEmpty();
    }

    @Test
    void returnsEmptyForHeaderShorterThanAnySignature() {
        assertThat(DocumentFormat.detect(new byte[] {0x50})).isEmpty();
    }
}
