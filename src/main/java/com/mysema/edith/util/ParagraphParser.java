/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.util;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.mysema.edith.domain.LinkElement;
import com.mysema.edith.domain.Paragraph;
import com.mysema.edith.domain.StringElement;
import com.mysema.edith.domain.UrlElement;

public final class ParagraphParser {

    public static final Paragraph parseSafe(String s) {
        if (s != null) {
            try {
                return parseParagraph(s);
            } catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private static final Paragraph parseParagraph(String s) throws XMLStreamException {
        String document = new StringBuilder("<root>").append(s).append("</root>").toString();
        Paragraph paragraph = new Paragraph();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(document));
        boolean inBib = false;
        boolean inA = false;
        String reference = null;
        // String href = null;
        while (true) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (reader.getLocalName().equals("bibliograph")) {
                    inBib = true;
                    if (reader.getAttributeCount() > 0) {
                        reference = reader.getAttributeValue(0);
                    }
                } else if (reader.getLocalName().equals("a")) {
                    inA = true;
                    if (reader.getAttributeCount() > 0) {
                        reference = reader.getAttributeValue(0);
                    }
                }
            } else if (event == XMLStreamConstants.CHARACTERS) {
                if (inBib) {
                    LinkElement element = new LinkElement(reader.getText());
                    if (reference != null) {
                        element.setReference(reference);
                    }
                    paragraph.addElement(element);
                } else if (inA) {
                    UrlElement element = new UrlElement(reader.getText());
                    if (reference != null) {
                        element.setUrl(reference);
                    }
                    paragraph.addElement(element);
                } else {
                    paragraph.addElement(new StringElement(reader.getText()));
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                inBib = false;
                inA = false;
                reference = null;
            } else if (event == XMLStreamConstants.END_DOCUMENT) {
                reader.close();
                break;
            }
        }
        return paragraph;
    }

    private ParagraphParser() {
    }
}
