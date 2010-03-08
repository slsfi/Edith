/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;

/**
 * DocumentWriterImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRendererImpl implements DocumentRenderer {

    private static final String XML_NS = "http://www.w3.org/XML/1998/namespace";

    static final Set<String> EMPTY_ELEMENTS = new HashSet<String>(Arrays.asList("anchor", "lb", "pb"));

    static final Set<String> UL_ELEMENTS = new HashSet<String>(Arrays.asList("castGroup","castList","listPerson"));

    static final Set<String> LI_ELEMENTS = new HashSet<String>(Arrays.asList("castItem","person"));

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private static final String CLASS = "class";

    private static final String DIV = "div";

    private final DocumentRepository documentRepo;

    private final XMLInputFactory inFactory = XMLInputFactory.newInstance();

    public DocumentRendererImpl(@Inject DocumentRepository documentRepo){
        this.documentRepo = documentRepo;
    }

    @Override
    public void renderPageLinks(DocumentRevision document, MarkupWriter writer) throws IOException, XMLStreamException {
        InputStream is = documentRepo.getDocumentStream(document);
        XMLStreamReader reader = inFactory.createXMLStreamReader(is);

        try{
            writer.element("ul", CLASS, "pages");
            while (true) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT){
                    String localName = reader.getLocalName();
                    if (localName.equals("pb")){
                        String page = reader.getAttributeValue(null, "n");
                        if (page != null){
                            writer.element("li");
                            writer.element("a", "href", "#page" + page);
                            writer.writeRaw(page);
                            writer.end();
                            writer.end();
                        }
                    }

                }else if (event == XMLStreamConstants.END_DOCUMENT) {
                    break;
                }
            }
            writer.end();
        }finally{
            reader.close();
            is.close();
        }

    }

    @Override
    public void renderDocument(DocumentRevision document, MarkupWriter writer) throws IOException, XMLStreamException {
        InputStream is = documentRepo.getDocumentStream(document);
        XMLStreamReader reader = inFactory.createXMLStreamReader(is);

        MutableBoolean noteContent = new MutableBoolean(false);
        Set<String> noteIds = new HashSet<String>();
        ElementContext context = new ElementContext(3);

        try {
            while (true) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    handleStartElement(reader, writer, context, noteIds, noteContent);
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    handleEndElement(reader, writer, context);
                } else if (event == XMLStreamConstants.CHARACTERS) {
                    handleCharactersElement(reader, writer, noteIds, noteContent);
                } else if (event == XMLStreamConstants.END_DOCUMENT) {
                    break;
                }
            }
        } finally {
            reader.close();
            is.close();
        }
    }

    private void handleStartElement(XMLStreamReader reader, MarkupWriter writer,
            ElementContext context, Set<String> noteIds, MutableBoolean noteContent) {
        String localName = reader.getLocalName();
        String name = extractName(reader, localName);
        context.push(name);
        String path = context.getPath();

        if (UL_ELEMENTS.contains(localName)) {
            writer.element("ul", CLASS, localName);
            if (path != null) {
                writer.attributes("id", path);
            }
        } else if (LI_ELEMENTS.contains(localName)) {
            writer.element("li", CLASS, localName);
            if (path != null) {
                writer.attributes("id", path);
            }

        } else if (localName.equals(DIV)) {
            String type = reader.getAttributeValue(null, "type");
            writer.element(localName, CLASS, type);
            if (path != null) {
                writer.attributes("id", path);
            }

        } else if (localName.equals("TEI")) {
            writer.element(DIV, CLASS, "tei");

        } else if (localName.equals("lb")) {
            writer.element("br");
            writer.end();

        } else if (localName.equals("pb")) {
            String page = reader.getAttributeValue(null, "n");
            if (page != null) {
                writer.element(DIV, "id", "page" + page, CLASS, "page");
                writer.writeRaw(page + ".");
                writer.end();
            }

        } else if (localName.equals("anchor")) {
            String id = reader.getAttributeValue(XML_NS, "id");
            if (id == null) {
                return;
            } else if (id.startsWith("start")) {
                // start anchor
                writer.element("span", CLASS, "notestart", "id", id);
                writer.end();

                noteContent.setValue(true);
                noteIds.add(id.substring("start".length()));
            } else if (id.startsWith("end")) {
                noteIds.remove(id.substring("end".length()));
                if (noteIds.isEmpty()) {
                    noteContent.setValue(false);
                }
            }
        } else {
            writer.element(DIV, CLASS, name);
            if (path != null) {
                writer.attributes("id", path);
            }
        }
    }

    private String extractName(XMLStreamReader reader, String localName) {
        if (localName.equals(DIV)) {
            return reader.getAttributeValue(null, "type");
        }
        return localName;
    }

    private void handleEndElement(XMLStreamReader reader, MarkupWriter writer,
            ElementContext context) {
        context.pop();
        String localName = reader.getLocalName();
        if (!EMPTY_ELEMENTS.contains(localName)) {
            writer.end();
        }
    }

    private void handleCharactersElement(XMLStreamReader reader, MarkupWriter writer,
            Set<String> noteIds, MutableBoolean noteContent) {
        String text = WHITESPACE.matcher(reader.getText()).replaceAll(" ");
        if (noteContent.booleanValue() && !text.trim().isEmpty()) {
            StringBuilder classes = new StringBuilder("notecontent");
            for (String noteId : noteIds) {
                classes.append(" n").append(noteId);
            }
            writer.element("span", CLASS, classes);
            writer.writeRaw(StringEscapeUtils.escapeXml(text));
            writer.end();
        } else {
            writer.writeRaw(StringEscapeUtils.escapeXml(text));
        }
    }
}
