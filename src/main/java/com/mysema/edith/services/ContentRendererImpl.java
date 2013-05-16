/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mysema.edith.EDITH;
import com.mysema.edith.domain.*;
import com.mysema.edith.util.ElementContext;
import com.mysema.edith.util.ParagraphParser;

/**
 * @author tiwe
 *
 */
public class ContentRendererImpl implements ContentRenderer {

    private static final String XML_NS = "http://www.w3.org/XML/1998/namespace";

    private static final Set<String> EMPTY_ELEMENTS = ImmutableSet.of("anchor", "lb", "pb");

    private static final Set<String> UL_ELEMENTS = ImmutableSet.of("castGroup", "castList", "listPerson");

    private static final Set<String> LI_ELEMENTS = ImmutableSet.of("castItem", "person");

    private static final String TYPE = "type";

    private static final String ANCHOR = "anchor";

    private static final String END = "end";

    private static final String SPAN = "span";

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private static final String CLASS = "class";

    private static final String DIV = "div";

    private final DocumentDao documentDao;

    private final XMLInputFactory inFactory = XMLInputFactory.newInstance();

    private final XMLOutputFactory outFactory = XMLOutputFactory.newInstance();

    private final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    private final String bibliographUrl;

    @Inject
    public ContentRendererImpl(DocumentDao documentDao,
            @Named(EDITH.BIBLIOGRAPH_URL) String bibliographUrl) {
        this.documentDao = documentDao;
        this.bibliographUrl = bibliographUrl;
    }

    private void writeSpan(XMLStreamWriter writer, String attr) throws XMLStreamException {
        writer.writeStartElement(SPAN);
        writer.writeAttribute(CLASS, attr);
    }

    private void writeNote(XMLStreamWriter writer, Note note) throws XMLStreamException {
        if (note.getLemmaMeaning() != null || note.getSubtextSources() != null) {
            writeSpan(writer, "lemmaMeaningAndSubtextSources");
            if (note.getLemmaMeaning() != null) {
                writer.writeCharacters("'" + note.getLemmaMeaning() + "'");
            }
            if (note.getLemmaMeaning() != null && note.getSubtextSources() != null) {
                writer.writeCharacters(", ");
            }
            if (note.getSubtextSources() != null) {
                writer.writeCharacters("Vrt. ");
                writeParagraph(writer, ParagraphParser.parseSafe(note.getSubtextSources()));
            }
            writer.writeEndElement();
        }
    }

    private void writePerson(XMLStreamWriter writer, Note note) throws XMLStreamException {
        Person person = note.getPerson();
        if (person != null) {
            writeSpan(writer, "personName");
            writer.writeCharacters(person.getNormalized().getName());

            Interval timeOfBirth = person.getTimeOfBirth();
            Interval timeOfDeath = person.getTimeOfDeath();

            if (timeOfBirth != null || timeOfDeath != null) {
                writer.writeCharacters(",");
                writer.writeEndElement();
                StringBuilder builder = new StringBuilder();
                if (timeOfBirth != null) {
                    builder.append(timeOfBirth.toString());
                }
                builder.append("\u2013");
                if (timeOfDeath != null) {
                    builder.append(timeOfDeath.toString());
                }
                builder.append(".");
                writeSpan(writer, "lifetime");
                writer.writeCharacters(builder.toString());
                writer.writeEndElement();
            } else {
                writer.writeCharacters(".");
                writer.writeEndElement();
            }
        }
    }

    private void writePlace(XMLStreamWriter writer, Note note) throws XMLStreamException {
        Place place = note.getPlace();
        if (place != null) {
            writeSpan(writer, "placeName");
            writer.writeCharacters(place.getNormalized().getName() + ".");
            writer.writeEndElement();
        }
    }

    @Override
    public void renderDocumentNotesAsXML(Document document, List<DocumentNote> documentNotes,
            XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("document");
        write(writer, "path", document.getPath());
        // TODO Would this be good?
        write(writer, "revision", -1);

        Map<Note, List<DocumentNote>> noteToDocumentNotes = new HashMap<Note, List<DocumentNote>>();
        for (DocumentNote documentNote : documentNotes) {
            List<DocumentNote> notes = noteToDocumentNotes.get(documentNote.getNote());
            if (notes == null) {
                notes = new ArrayList<DocumentNote>();
                noteToDocumentNotes.put(documentNote.getNote(), notes);
            }
            notes.add(documentNote);
        }

        for (Map.Entry<Note, List<DocumentNote>> entry : noteToDocumentNotes.entrySet()) {
            Note note = entry.getKey();
            writer.writeStartElement("note");
            writer.writeAttribute("xml:id", "note" + note.getId());
            write(writer, "description", note.getDescription());
            write(writer, "format", note.getFormat());
            write(writer, "lemma", note.getLemma());
            write(writer, "lemmaMeaning", note.getLemmaMeaning());
            if (note.getPerson() != null) {
                writer.writeStartElement("person");
                Person person = note.getPerson();
                writeNameForm("normalizedForm", person.getNormalized(), writer);
                writer.writeStartElement("otherForms");
                for (NameForm otherForm : person.getOtherForms()) {
                    writeNameForm("nameForm", otherForm, writer);
                }
                // TODO : timeOfBirth
                // TODO : timeOfDeath
                writer.writeEndElement(); // otherForms
                writer.writeEndElement(); // person
            }
            if (note.getPlace() != null) {
                writer.writeStartElement("place");
                Place place = note.getPlace();
                writeNameForm("normalizedForm", place.getNormalized(), writer);
                writer.writeStartElement("otherForms");
                for (NameForm otherForm : place.getOtherForms()) {
                    writeNameForm("nameForm", otherForm, writer);
                }
                writer.writeEndElement(); // otherForms
                writer.writeEndElement(); // place
            }
            write(writer, "sources", note.getSources());
            write(writer, "subtextSources", note.getSubtextSources());
            if (!note.getTypes().isEmpty()) {
                writer.writeStartElement("types");
                for (NoteType type : note.getTypes()) {
                    write(writer, TYPE, type);
                }
                writer.writeEndElement();
            }

            writer.writeStartElement("documentNotes");
            for (DocumentNote dn : entry.getValue()) {
                writer.writeStartElement("documentNote");
                writer.writeAttribute("xml:id", END + dn.getId());
                write(writer, "longText", dn.getFullSelection());
                write(writer, "svnRevision", dn.getRevision());
                write(writer, "createdOn", dn.getCreatedOn());
                writer.writeEndElement(); // documentNote
            }
            writer.writeEndElement(); // documentNotes
            writer.writeEndElement(); // note
        }

        writer.writeEndElement(); // document
    }

    private void writeNameForm(String name, NameForm nameForm, XMLStreamWriter writer) throws XMLStreamException {
        if (nameForm != null) {
            writer.writeStartElement(name);
            write(writer, "description", nameForm.getDescription());
            write(writer, "first", nameForm.getFirst());
            write(writer, "last", nameForm.getLast());
            writer.writeEndElement();
        }
    }

    private void write(XMLStreamWriter writer, String element, Object content) throws XMLStreamException {
        if (content != null) {
            writer.writeStartElement(element);
            writer.writeCharacters(content.toString());
            writer.writeEndElement();
        }
    }

    @Override
    public void renderDocumentNotes(List<DocumentNote> documentNotes, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("ul");
        writer.writeAttribute(CLASS, "notes");
        for (DocumentNote documentNote : documentNotes) {
            Note note = documentNote.getNote();

            if (note == null) {
                throw new IllegalStateException("Got no note for documentNote " + documentNote);
            }

            writer.writeStartElement("li");
            writer.writeStartElement("a");            
            writer.writeAttribute("href", "#start" + documentNote.getId());
            writer.writeAttribute(CLASS, "notelink");
            if (note.getLemma() != null) {
                writer.writeStartElement(SPAN);
                writer.writeAttribute(CLASS, "lemma");
                writer.writeCharacters(note.getLemma());
                writer.writeEndElement();
            }
            if (note.getTerm() != null && note.getTerm().getBasicForm() != null) {
                writer.writeStartElement(SPAN);
                writer.writeAttribute(CLASS, "basicForm");
                writer.writeCharacters(note.getTerm().getBasicForm());
                writer.writeEndElement();
            }
            writer.writeEndElement();

            if (note.getFormat() != null) {
                if (note.getFormat().equals(NoteFormat.NOTE)) {
                    writeNote(writer, note);
                }
                if (note.getFormat().equals(NoteFormat.PERSON)) {
                    writePerson(writer, note);
                }

                if (note.getFormat().equals(NoteFormat.PLACE)) {
                    writePlace(writer, note);
                }
            }

            if (note.getDescription() != null) {
                if (note.getFormat() != null && !note.getFormat().equals(NoteFormat.NOTE)) {
                    writer.writeStartElement(SPAN);
                    writer.writeCharacters("\u2013");
                    writer.writeEndElement();
                }
                writeSpan(writer, "description");
                writeParagraph(writer, ParagraphParser.parseSafe(note.getDescription()));
                writer.writeEndElement();
            }
            if (note.getSources() != null) {
                writeSpan(writer, "sources");
                writer.writeCharacters("(");
                writeParagraph(writer, ParagraphParser.parseSafe(note.getSources()));
                writer.writeCharacters(")");
                writer.writeEndElement();
            }
            writer.writeEndElement();
        }
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
    }

    private void writeParagraph(XMLStreamWriter writer, Paragraph paragraph) throws XMLStreamException {
//        StringBuilder builder = new StringBuilder();
        for (ParagraphElement element : paragraph.getElements()) {
            if (element instanceof LinkElement) {
                LinkElement linkElement = (LinkElement) element;
//                String reference = StringEscapeUtils.escapeHtml4(linkElement.getReference());
//                String string = StringEscapeUtils.escapeHtml4(linkElement.getString());
//                String result = "<a"
//                        + (reference == null ? "" : " href=\"" + bibliographUrl + reference + "\"")
//                        + ">" + string + "</a>";
//                builder.append(result);
                writer.writeStartElement("a");
                if (linkElement.getReference() != null) {
                    writer.writeAttribute("href", bibliographUrl + linkElement.getReference());                    
                }
                writer.writeCharacters(linkElement.getString());
                writer.writeEndElement();
            } else if (element instanceof UrlElement) {
                UrlElement urlElement = (UrlElement) element;
//                String url = StringEscapeUtils.escapeHtml4(urlElement.getUrl());
//                String string = StringEscapeUtils.escapeHtml4(urlElement.getString());
//                String result = "<a" + (url == null ? "" : " href=\"" + url + "\"") + ">" + string
//                        + "</a>";
//                builder.append(result);
                writer.writeStartElement("a");
                if (urlElement.getUrl() != null) {
                    writer.writeAttribute("href", urlElement.getUrl());
                }
                writer.writeCharacters(urlElement.getString());
                writer.writeEndElement();
            } else {
                writer.writeCharacters(element.toString());
            }
        }
    }

    @Override
    public void renderPageLinks(Document document, XMLStreamWriter writer) throws IOException,
            XMLStreamException {
        InputStream is = documentDao.getDocumentStream(document);
        XMLStreamReader reader = inFactory.createXMLStreamReader(is);

        try {
            writer.writeStartDocument();
            writer.writeStartElement("ul");
            writer.writeAttribute(CLASS, "pages");
            while (true) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String localName = reader.getLocalName();
                    if (localName.equals("pb")) {
                        String page = reader.getAttributeValue(null, "n");
                        if (page != null) {
                            writer.writeStartElement("li");
                            writer.writeStartElement("a");
                            writer.writeAttribute("href", "#page"+ page);
                            writer.writeCharacters(page);
                            writer.writeEndElement();
                            writer.writeEndElement();
                        }
                    }

                } else if (event == XMLStreamConstants.END_DOCUMENT) {
                    break;
                }
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
        } finally {
            reader.close();
            is.close();
        }
    }

    @Override
    public void renderDocument(Document document, XMLStreamWriter writer) throws IOException,
            XMLStreamException {
        renderDocument(document, null, writer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void renderDocumentAsXML(Document document, List<DocumentNote> documentNotes,
            OutputStream out) throws IOException, XMLStreamException {
        InputStream is = documentDao.getDocumentStream(document);
        XMLEventReader reader = inFactory.createXMLEventReader(is);
        XMLEventWriter writer = outFactory.createXMLEventWriter(out);

        QName note = new QName("note");
        XMLEvent openAnchor = null;
        try {
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    Iterator<Attribute> attributes = startElement.getAttributes();
                    if (startElement.getName().getLocalPart().equals(ANCHOR)
                            && attributes.next().getValue().startsWith(END)) {
                        String id = ((Iterator<Attribute>) startElement.getAttributes()).next()
                                .getValue().substring(3);
                        List<Attribute> atts = new ArrayList<Attribute>();
                        atts.add(eventFactory.createAttribute(TYPE, "editor"));
                        atts.add(eventFactory.createAttribute("xml:id", END + id));
                        atts.add(eventFactory.createAttribute("target", "#start" + id));
                        event = eventFactory.createStartElement(note, atts.iterator(), null);
                        openAnchor = event;
                    } else {
                        openAnchor = null;
                    }
                } else if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (openAnchor != null && endElement.getName().getLocalPart().equals(ANCHOR)) {
                        event = eventFactory.createEndElement(note, null);
                    }
                    openAnchor = null;
                }
                writer.add(event);
            }
        } finally {
            writer.close();
            out.close();
            reader.close();
            is.close();
        }
    }

    @Override
    public void renderDocument(Document document, List<DocumentNote> documentNotes,
            XMLStreamWriter writer) throws IOException, XMLStreamException {
        Set<Long> publishIds = null;
        if (documentNotes != null) {
            publishIds = new HashSet<Long>();
            for (DocumentNote documentNote : documentNotes) {
                publishIds.add(documentNote.getId());
            }
        }
        InputStream is = documentDao.getDocumentStream(document);
        XMLStreamReader reader = inFactory.createXMLStreamReader(is);

        AtomicBoolean noteContent = new AtomicBoolean(false);
        Set<Long> noteIds = new HashSet<Long>();
        ElementContext context = new ElementContext();

        try {
            while (true) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    handleStartElement(reader, writer, context, noteIds, noteContent, publishIds);
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    handleEndElement(reader, writer, context);
                } else if (event == XMLStreamConstants.CHARACTERS) {
                    handleCharactersElement(reader, writer, noteIds, context, noteContent);
                } else if (event == XMLStreamConstants.END_DOCUMENT) {
                    break;
                }
            }
        } finally {
            reader.close();
            is.close();
        }
    }

    private void handleStartElement(XMLStreamReader reader, XMLStreamWriter writer,
            ElementContext context, Set<Long> noteIds, AtomicBoolean noteContent,
            Set<Long> publishIds) throws XMLStreamException {
        String localName = reader.getLocalName();
        String name = extractName(reader, localName);
        if (!name.equals("anchor")) {
            context.push(name);    
        }        
        String path = context.getPath();
        String rend = reader.getAttributeValue(null, "rend");

        if (UL_ELEMENTS.contains(localName)) {
            writer.writeStartElement("ul");
            writer.writeAttribute(CLASS, localName);
            if (path != null) {
                writer.writeAttribute("id", path);
            }
        } else if (LI_ELEMENTS.contains(localName)) {
            writer.writeStartElement("li");
            writer.writeAttribute(CLASS, localName);
            if (path != null) {
                writer.writeAttribute("id", path);
            }
        } else if (localName.equals(DIV)) {
            String type = reader.getAttributeValue(null, TYPE);
            writer.writeStartElement(localName);
            writer.writeAttribute(CLASS, type);
            if (path != null) {
                writer.writeAttribute("id", path);
            }
        } else if (localName.equals("TEI") || localName.equals("TEI.2")) {
            writer.writeStartElement(DIV);
            writer.writeAttribute(CLASS, "tei");
        } else if (localName.equals("lb")) {
            writer.writeStartElement("br");
            writer.writeEndElement();
        } else if (localName.equals("pb")) {
            String page = reader.getAttributeValue(null, "n");
            if (page != null) {
                String type = reader.getAttributeValue(null, "type");
                String cssClass = type != null ? ("page " + type) : "page";
                writer.writeStartElement(DIV);
                writer.writeAttribute("id", "page"+page);
                writer.writeAttribute(CLASS, cssClass);
                writer.writeCharacters(page + ".");
                writer.writeEndElement();
            }
        } else if (localName.equals(ANCHOR)) {
            String id = reader.getAttributeValue(XML_NS, "id");
            if (id == null) {
                return;
            } else if (id.startsWith("start")) {
                if (publishIds != null
                        && !publishIds.contains(Long.parseLong(id.replace("start", "")))) {
                    return;
                }
                // start anchor
                writer.writeStartElement(SPAN);
                writer.writeAttribute(CLASS, "notestart");
                writer.writeAttribute("data-node", context.getPath());
                writer.writeAttribute("id", id);
                writer.writeEndElement();

                noteContent.set(true);
                noteIds.add(Long.parseLong(id.substring("start".length())));
            } else if (id.startsWith(END)) {
                writer.writeStartElement(SPAN);
                writer.writeAttribute("class", "noteanchor");
                writer.writeAttribute("data-node", context.getPath());
                writer.writeAttribute("id", id);
                writer.writeEndElement();

                noteIds.remove(Long.parseLong(id.substring(END.length())));
                if (noteIds.isEmpty()) {
                    noteContent.set(false);
                }
            }
        } else if (localName.equals("milestone")) {
            StringBuilder cssClass = new StringBuilder(name);
            String type = reader.getAttributeValue(null, "type");
            if (type != null) {
                cssClass.append(" " + type);
            }
            String unit = reader.getAttributeValue(null, "unit");
            if (unit != null) {
                cssClass.append(" " + unit);
            }
            writer.writeStartElement(DIV);
            writer.writeAttribute(CLASS, cssClass.toString());

        } else {
            String cssClass = rend != null ? (name + " " + rend) : name;
            writer.writeStartElement(DIV);
            writer.writeAttribute(CLASS, cssClass);
            if (path != null) {
                writer.writeAttribute("id", path);
            }

        }
    }

    private String extractName(XMLStreamReader reader, String localName) {
//        if (localName.equals(DIV)) {
//            return reader.getAttributeValue(null, TYPE);
//        }
        return localName;
    }

    private void handleEndElement(XMLStreamReader reader, XMLStreamWriter writer,
            ElementContext context) throws XMLStreamException {
        String localName = reader.getLocalName();
        if (!localName.equals("anchor")) {
            context.pop();
        }
        if (!EMPTY_ELEMENTS.contains(localName)) {
            writer.writeEndElement();
        }
    }

    private void handleCharactersElement(XMLStreamReader reader, XMLStreamWriter writer,
            Set<Long> noteIds, ElementContext context, AtomicBoolean noteContent) throws XMLStreamException {
        String text = WHITESPACE.matcher(reader.getText()).replaceAll(" ");
        if (noteContent.get() && !text.trim().isEmpty()) {
            StringBuilder classes = new StringBuilder("notecontent");
            for (Long noteId : noteIds) {
                classes.append(" n").append(noteId);
            }
            writer.writeStartElement(SPAN);
            writer.writeAttribute(CLASS, classes.toString());
            writer.writeAttribute("data-node", context.getPath());
            writer.writeCharacters(text);
            writer.writeEndElement();
        } else {
            writer.writeCharacters(text);
        }
    }

}
