package com.jasper.documentmatcher.document;

import java.io.InputStream;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Extracts plain text from an XML document by concatenating all text nodes, e.g. for HR system
 * exports where the readable content lives in element text rather than in a rendered layout.
 */
@Component
class XmlTextExtractor implements DocumentTextExtractor {

    private static final Logger log = LoggerFactory.getLogger(XmlTextExtractor.class);

    @Override
    public boolean supports(DocumentFormat format) {
        return format == DocumentFormat.XML;
    }

    @Override
    public DocumentExtractionResult extract(InputStream content) {
        try {
            var reader = SafeXmlInputFactory.create().createXMLStreamReader(content);
            var text = collectText(reader);
            return text.isBlank()
                    ? DocumentExtractionResult.of(DocumentExtractionStatus.EMPTY)
                    : DocumentExtractionResult.success(text);
        } catch (XMLStreamException e) {
            log.debug("XML text extraction completed with status {}", DocumentExtractionStatus.CORRUPTED);
            return DocumentExtractionResult.of(DocumentExtractionStatus.CORRUPTED);
        }
    }

    private String collectText(XMLStreamReader reader) throws XMLStreamException {
        var builder = new StringBuilder();
        while (reader.hasNext()) {
            if (reader.next() == XMLStreamConstants.CHARACTERS && !reader.isWhiteSpace()) {
                builder.append(reader.getText().strip()).append(' ');
            }
        }
        return builder.toString().strip();
    }
}
