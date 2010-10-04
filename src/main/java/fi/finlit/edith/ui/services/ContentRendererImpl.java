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
import java.util.List;
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

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Interval;
import fi.finlit.edith.domain.LinkElement;
import fi.finlit.edith.domain.NoteFormat;
import fi.finlit.edith.domain.Paragraph;
import fi.finlit.edith.domain.ParagraphElement;
import fi.finlit.edith.domain.Person;
import fi.finlit.edith.domain.Place;

/**
 * DocumentWriterImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class ContentRendererImpl implements ContentRenderer {

    private static final String XML_NS = "http://www.w3.org/XML/1998/namespace";

    static final Set<String> EMPTY_ELEMENTS = new HashSet<String>(Arrays.asList("anchor", "lb",
            "pb"));

    static final Set<String> UL_ELEMENTS = new HashSet<String>(Arrays.asList("castGroup",
            "castList", "listPerson"));

    static final Set<String> LI_ELEMENTS = new HashSet<String>(Arrays.asList("castItem", "person"));

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private static final String CLASS = "class";

    private static final String DIV = "div";

    private final DocumentRepository documentRepository;

    private final XMLInputFactory inFactory = XMLInputFactory.newInstance();

    public ContentRendererImpl(@Inject DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public void renderDocumentNotes(List<DocumentNote> documentNotes, MarkupWriter writer) {
        writer.element("ul", CLASS, "notes");
        for (DocumentNote documentNote : documentNotes) {
            writer.element("a", CLASS, "notelink", "href", "#start" + documentNote.getLocalId());
            writer.element("em");
            writer.write(documentNote.getNote().getLemma());
            writer.end();
            writer.end();

            if (documentNote.getNote().getFormat() != null) {
                if (documentNote.getNote().getFormat().equals(NoteFormat.NOTE)) {
                    if (documentNote.getNote().getLemmaMeaning() != null) {
                        writer.write(", '" + documentNote.getNote().getLemmaMeaning() + "'");
                    }
                    if (documentNote.getNote().getSubtextSources() != null) {
                        writer.write(", Vrt. ");
                        writeParagraph(writer, documentNote.getNote().getSubtextSources());
                    }
                }

                if (documentNote.getNote().getFormat().equals(NoteFormat.PERSON)) {
                    Person person = documentNote.getNote().getPerson();
                    if (person != null) {
                        writer.write(", " + person.getNormalizedForm().getFirst());
                        writer.write(", " + person.getNormalizedForm().getLast());
                        Interval timeOfBirth = person.getTimeOfBirth();
                        Interval timeOfDeath = person.getTimeOfDeath();
                        if (timeOfBirth != null || timeOfDeath != null) {
                            StringBuilder builder = new StringBuilder();
                            if (timeOfBirth != null) {
                                builder.append(timeOfBirth.asString());
                            }
                            builder.append("\u2013");
                            if (timeOfDeath != null) {
                                builder.append(timeOfDeath.asString());
                            }
                            builder.append(".");
                            writer.write(", " + builder.toString());
                        }
                    }
                }

                if (documentNote.getNote().getFormat().equals(NoteFormat.PLACE)) {
                    Place place = documentNote.getNote().getPlace();
                    if (place != null) {
                        writer.write(", " + place.getNormalizedForm().getName());
                    }
                }
            }

            if (documentNote.getNote().getDescription() != null) {
                writer.write(", ");
                writeParagraph(writer, documentNote.getNote().getDescription());
            }
            if (documentNote.getNote().getSources() != null) {
                writer.write(", (");
                writeParagraph(writer, documentNote.getNote().getSources());
                writer.write(")");
            }
        }
        writer.end();
    }

    private void writeParagraph(MarkupWriter writer, Paragraph paragraph) {
        String root = "";
        StringBuilder builder = new StringBuilder();
        for (ParagraphElement element : paragraph.getElements()) {
            if (element instanceof LinkElement) {
                LinkElement linkElement = (LinkElement) element;
                String reference = StringEscapeUtils.escapeHtml(linkElement.getReference());
                String string = StringEscapeUtils.escapeHtml(linkElement.getString());
                String result = "<a"
                        + (reference == null ? "" : " href=\"" + root + reference + "\"") + ">"
                        + string + "</a>";
                builder.append(result);
            } else {
                builder.append(element.toString());
            }
        }
        writer.writeRaw(builder.toString());
    }

    @Override
    public void renderPageLinks(DocumentRevision document, MarkupWriter writer) throws IOException,
            XMLStreamException {
        InputStream is = documentRepository.getDocumentStream(document);
        XMLStreamReader reader = inFactory.createXMLStreamReader(is);

        try {
            writer.element("ul", CLASS, "pages");
            while (true) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String localName = reader.getLocalName();
                    if (localName.equals("pb")) {
                        String page = reader.getAttributeValue(null, "n");
                        if (page != null) {
                            writer.element("li");
                            writer.element("a", "href", "#page" + page);
                            writer.writeRaw(page);
                            writer.end();
                            writer.end();
                        }
                    }

                } else if (event == XMLStreamConstants.END_DOCUMENT) {
                    break;
                }
            }
            writer.end();
        } finally {
            reader.close();
            is.close();
        }

    }

    @Override
    public void renderDocument(DocumentRevision document, MarkupWriter writer) throws IOException,
            XMLStreamException {
        InputStream is = documentRepository.getDocumentStream(document);
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
