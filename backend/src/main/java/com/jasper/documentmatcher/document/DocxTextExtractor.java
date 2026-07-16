package com.jasper.documentmatcher.document;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Extracts plain text from a .docx file. A .docx is a ZIP archive; the document body lives in the
 * {@code word/document.xml} entry as a sequence of {@code <w:p>} paragraphs of {@code <w:t>} text
 * runs. This reads that one entry directly rather than pulling in Apache POI, which would add a
 * large dependency (xmlbeans, commons-compress, ...) for a feature this narrow.
 */
@Component
class DocxTextExtractor implements DocumentTextExtractor {

    private static final String DOCUMENT_ENTRY = "word/document.xml";
    private static final Logger log = LoggerFactory.getLogger(DocxTextExtractor.class);

    @Override
    public boolean supports(DocumentFormat format) {
        return format == DocumentFormat.DOCX;
    }

    @Override
    public DocumentExtractionResult extract(InputStream content) {
        try (var zip = new ZipInputStream(content)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                // Normalize backslashes: the ZIP spec requires forward slashes, but some
                // non-compliant zip tools (e.g. PowerShell's Compress-Archive on Windows) use
                // backslashes instead.
                if (DOCUMENT_ENTRY.equals(entry.getName().replace('\\', '/'))) {
                    var text = collectText(zip);
                    return text.isBlank()
                            ? DocumentExtractionResult.of(DocumentExtractionStatus.EMPTY)
                            : DocumentExtractionResult.success(text);
                }
            }
            log.debug("DOCX text extraction failed: {} entry not found", DOCUMENT_ENTRY);
            return DocumentExtractionResult.of(DocumentExtractionStatus.CORRUPTED);
        } catch (IOException | XMLStreamException e) {
            log.debug("DOCX text extraction completed with status {}", DocumentExtractionStatus.CORRUPTED);
            return DocumentExtractionResult.of(DocumentExtractionStatus.CORRUPTED);
        }
    }

    private String collectText(InputStream documentXml) throws XMLStreamException {
        var reader = SafeXmlInputFactory.create().createXMLStreamReader(documentXml);
        var builder = new StringBuilder();
        var inTextRun = false;

        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT -> inTextRun = isTextElement(reader);
                case XMLStreamConstants.CHARACTERS -> {
                    if (inTextRun) {
                        builder.append(reader.getText());
                    }
                }
                case XMLStreamConstants.END_ELEMENT -> {
                    if (isTextElement(reader)) {
                        inTextRun = false;
                    } else if (isParagraphElement(reader)) {
                        builder.append('\n');
                    }
                }
                default -> {
                    // other event types (e.g. START_DOCUMENT, COMMENT) carry no text content
                }
            }
        }

        return builder.toString().strip();
    }

    private boolean isTextElement(XMLStreamReader reader) {
        return "t".equals(reader.getLocalName());
    }

    private boolean isParagraphElement(XMLStreamReader reader) {
        return "p".equals(reader.getLocalName());
    }
}
