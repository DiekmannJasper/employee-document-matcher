package com.jasper.documentmatcher.document;

import javax.xml.stream.XMLInputFactory;

/**
 * A shared {@link XMLInputFactory} with DTD processing and external entity resolution disabled,
 * so parsing an uploaded .docx or .xml file cannot be used for an XXE attack.
 */
final class SafeXmlInputFactory {

    private SafeXmlInputFactory() {
    }

    static XMLInputFactory create() {
        var factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        return factory;
    }
}
