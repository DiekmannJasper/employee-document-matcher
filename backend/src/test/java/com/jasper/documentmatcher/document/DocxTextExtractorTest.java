package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;

class DocxTextExtractorTest {

    private final DocxTextExtractor extractor = new DocxTextExtractor();

    @Test
    void supportsOnlyDocx() {
        assertThat(extractor.supports(DocumentFormat.DOCX)).isTrue();
        assertThat(extractor.supports(DocumentFormat.PDF)).isFalse();
        assertThat(extractor.supports(DocumentFormat.XML)).isFalse();
    }

    @Test
    void extractsTextFromParagraphsAndRuns() throws IOException {
        var documentXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
                    <w:body>
                        <w:p><w:r><w:t>Arbeitsvertrag für</w:t></w:r><w:r><w:t> Anna Müller</w:t></w:r></w:p>
                        <w:p><w:r><w:t>Zweiter Absatz</w:t></w:r></w:p>
                    </w:body>
                </w:document>
                """;

        var result = extractor.extract(docxContaining(documentXml));

        assertThat(result.status()).isEqualTo(DocumentExtractionStatus.SUCCESS);
        assertThat(result.text()).contains("Arbeitsvertrag für Anna Müller").contains("Zweiter Absatz");
    }

    @Test
    void reportsEmptyForDocxWithoutText() throws IOException {
        var documentXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
                    <w:body><w:p/></w:body>
                </w:document>
                """;

        var result = extractor.extract(docxContaining(documentXml));

        assertThat(result.status()).isEqualTo(DocumentExtractionStatus.EMPTY);
        assertThat(result.text()).isNull();
    }

    @Test
    void reportsCorruptedWhenDocumentXmlEntryIsMissing() throws IOException {
        var output = new ByteArrayOutputStream();
        try (var zip = new ZipOutputStream(output)) {
            zip.putNextEntry(new ZipEntry("some-other-file.xml"));
            zip.write("<root/>".getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }

        var result = extractor.extract(new ByteArrayInputStream(output.toByteArray()));

        assertThat(result.status()).isEqualTo(DocumentExtractionStatus.CORRUPTED);
        assertThat(result.text()).isNull();
    }

    @Test
    void toleratesBackslashSeparatedZipEntryNames() throws IOException {
        // The ZIP spec requires forward slashes, but some non-compliant tools (e.g. PowerShell's
        // Compress-Archive on Windows) write backslashes instead - a real-world docx from Word,
        // LibreOffice, or Google Docs always uses forward slashes.
        var documentXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
                    <w:body><w:p><w:r><w:t>Arbeitsvertrag</w:t></w:r></w:p></w:body>
                </w:document>
                """;
        var output = new ByteArrayOutputStream();
        try (var zip = new ZipOutputStream(output)) {
            zip.putNextEntry(new ZipEntry("word\\document.xml"));
            zip.write(documentXml.getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }

        var result = extractor.extract(new ByteArrayInputStream(output.toByteArray()));

        assertThat(result.status()).isEqualTo(DocumentExtractionStatus.SUCCESS);
        assertThat(result.text()).contains("Arbeitsvertrag");
    }

    @Test
    void reportsCorruptedForNonZipBytes() {
        var result = extractor.extract(new ByteArrayInputStream("not a zip".getBytes(StandardCharsets.UTF_8)));

        assertThat(result.status()).isEqualTo(DocumentExtractionStatus.CORRUPTED);
        assertThat(result.text()).isNull();
    }

    private ByteArrayInputStream docxContaining(String documentXml) throws IOException {
        var output = new ByteArrayOutputStream();
        try (var zip = new ZipOutputStream(output)) {
            zip.putNextEntry(new ZipEntry("word/document.xml"));
            zip.write(documentXml.getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }
        return new ByteArrayInputStream(output.toByteArray());
    }
}
