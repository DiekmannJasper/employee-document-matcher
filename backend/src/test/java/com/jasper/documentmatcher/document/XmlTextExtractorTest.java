package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class XmlTextExtractorTest {

    private final XmlTextExtractor extractor = new XmlTextExtractor();

    @Test
    void supportsOnlyXml() {
        assertThat(extractor.supports(DocumentFormat.XML)).isTrue();
        assertThat(extractor.supports(DocumentFormat.PDF)).isFalse();
        assertThat(extractor.supports(DocumentFormat.DOCX)).isFalse();
    }

    @Test
    void extractsConcatenatedTextNodes() {
        var xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <employee>
                    <name>Anna Müller</name>
                    <document>Arbeitsvertrag</document>
                </employee>
                """;

        var result = extractor.extract(inputStream(xml));

        assertThat(result.status()).isEqualTo(DocumentExtractionStatus.SUCCESS);
        assertThat(result.text()).contains("Anna Müller").contains("Arbeitsvertrag");
    }

    @Test
    void reportsEmptyForXmlWithoutTextContent() {
        var xml = "<?xml version=\"1.0\"?><root><empty/></root>";

        var result = extractor.extract(inputStream(xml));

        assertThat(result.status()).isEqualTo(DocumentExtractionStatus.EMPTY);
        assertThat(result.text()).isNull();
    }

    @Test
    void reportsCorruptedForMalformedXml() {
        var result = extractor.extract(inputStream("<root><unclosed>"));

        assertThat(result.status()).isEqualTo(DocumentExtractionStatus.CORRUPTED);
        assertThat(result.text()).isNull();
    }

    private ByteArrayInputStream inputStream(String xml) {
        return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    }
}
