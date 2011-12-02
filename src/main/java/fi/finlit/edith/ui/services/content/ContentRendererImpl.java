/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.sql.domain.*;
import fi.finlit.edith.ui.services.DocumentDao;
import fi.finlit.edith.util.ElementContext;
import fi.finlit.edith.util.ParagraphParser;

public class ContentRendererImpl implements ContentRenderer {

    private static final String XML_NS = "http://www.w3.org/XML/1998/namespace";

    private static final Set<String> EMPTY_ELEMENTS = new HashSet<String>(Arrays.asList("anchor", "lb", "pb"));

    private static final Set<String> UL_ELEMENTS = new HashSet<String>(Arrays.asList("castGroup", "castList", "listPerson"));

    private static final Set<String> LI_ELEMENTS = new HashSet<String>(Arrays.asList("castItem", "person"));

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

    public ContentRendererImpl(@Inject DocumentDao documentDao,
            @Inject @Symbol(EDITH.BIBLIOGRAPH_URL) String bibliographUrl
           ) {
        this.documentDao = documentDao;
        this.bibliographUrl = bibliographUrl;
    }

    private void writeSpan(MarkupWriter writer, String attr) {
        writer.element("SPAN", CLASS, attr);
    }

    private void writeNote(MarkupWriter writer, Note note) {
        if (note.getLemmaMeaning() != null || note.getSubtextSources() != null) {
            writeSpan(writer, "lemmaMeaningAndSubtextSources");
            if (note.getLemmaMeaning() != null) {
                writer.write("'" + note.getLemmaMeaning() + "'");
            }
            if (note.getLemmaMeaning() != null && note.getSubtextSources() != null) {
                writer.write(", ");
            }
            if (note.getSubtextSources() != null) {
                writer.write("Vrt. ");
                writeParagraph(writer, ParagraphParser.parseSafe(note.getSubtextSources()));
            }
            writer.end();
        }
    }

    private void writePerson(MarkupWriter writer, Note note) {
        Person person = note.getPerson();
        if (person != null) {
            writeSpan(writer, "personName");
            writer.write(person.getNormalized().getName());

            Interval timeOfBirth = person.getTimeOfBirth();
            Interval timeOfDeath = person.getTimeOfDeath();

            if (timeOfBirth != null || timeOfDeath != null) {
                writer.write(",");
                writer.end();
                StringBuilder builder = new StringBuilder();
                if (timeOfBirth != null) {
                    builder.append(timeOfBirth.asString());
                }
                builder.append("\u2013");
                if (timeOfDeath != null) {
                    builder.append(timeOfDeath.asString());
                }
                builder.append(".");
                writeSpan(writer, "lifetime");
                writer.write(builder.toString());
                writer.end();
            } else {
                writer.write(".");
                writer.end();
            }
        }
    }

    private void writePlace(MarkupWriter writer, Note note) {
        Place place = note.getPlace();
        if (place != null) {
            writeSpan(writer, "placeName");
            writer.write(place.getNormalized().getName() + ".");
            writer.end();
        }
    }


    @Override
    public void renderDocumentNotesAsXML(Document document, List<DocumentNote> documentNotes, MarkupWriter writer) {
        writer.element("document");
        write(writer, "path", document.getPath());
        //TODO Would this be good?
        write(writer, "revision", -1);

        Map<Note, List<DocumentNote>> noteToDocumentNotes = new HashMap<Note, List<DocumentNote>>();
        for (DocumentNote documentNote : documentNotes){
            List<DocumentNote> notes = noteToDocumentNotes.get(documentNote.getNote());
            if (notes == null){
                notes = new ArrayList<DocumentNote>();
                noteToDocumentNotes.put(documentNote.getNote(), notes);
            }
            notes.add(documentNote);
        }

        for (Map.Entry<Note, List<DocumentNote>> entry : noteToDocumentNotes.entrySet()){
          Note note = entry.getKey();
          writer.element("note", "xml:id", "note"+note.getId());
          write(writer, "description", note.getDescription());
          write(writer, "format", note.getFormat());
          write(writer, "lemma", note.getLemma());
          write(writer, "lemmaMeaning", note.getLemmaMeaning());
          if (note.getPerson() != null){
              writer.element("person");
              Person person = note.getPerson();
              writeNameForm("normalizedForm", person.getNormalized(), writer);
              writer.element("otherForms");
              for (NameForm otherForm : person.getOtherForms()){
                  writeNameForm("nameForm", otherForm, writer);
              }
              // TODO : timeOfBirth
              // TODO : timeOfDeath
              writer.end(); // otherForms
              writer.end(); // person
          }
          if (note.getPlace() != null){
              writer.element("place");
              Place place = note.getPlace();
              writeNameForm("normalizedForm", place.getNormalized(), writer);
              writer.element("otherForms");
              for (NameForm otherForm : place.getOtherForms()){
                  writeNameForm("nameForm", otherForm, writer);
              }
              writer.end(); // otherForms
              writer.end(); // place
          }
          write(writer, "sources", note.getSources());
          write(writer, "subtextSources", note.getSubtextSources());
          if (!note.getTypes().isEmpty()){
              writer.element("types");
              for (NoteType type : note.getTypes()){
                  write(writer, TYPE, type);
              }
              writer.end();
          }

          writer.element("documentNotes");
          for (DocumentNote dn : entry.getValue()){
              writer.element("documentNote", "xml:id", END+dn.getId());
              write(writer, "longText", dn.getFullSelection());
              write(writer, "svnRevision", dn.getRevision());
              write(writer, "createdOn", dn.getCreatedOn());
              writer.end(); // documentNote
          }
          writer.end(); // documentNotes

          writer.end(); // note
        }


        writer.end(); // document

    }

    private void writeNameForm(String name, NameForm nameForm, MarkupWriter writer) {
        if (nameForm != null){
            writer.element(name);
            write(writer, "description", nameForm.getDescription());
            write(writer, "first", nameForm.getFirst());
            write(writer, "last", nameForm.getLast());
            writer.end();
        }
    }

    private void write(MarkupWriter writer, String element, Object content){
        if (content != null){
            writer.element(element);
            writer.write(content.toString());
            writer.end();
        }
    }

    @Override
    public void renderDocumentNotes(List<DocumentNote> documentNotes, MarkupWriter writer) {
        writer.element("ul", CLASS, "notes");
        for (DocumentNote documentNote : documentNotes) {
            Note note = documentNote.getNote();

            if (note == null){
                throw new IllegalStateException("Got no note for documentNote " + documentNote);
            }

            writer.element("li");
            writer.element("a", CLASS, "notelink", "href", "#start" + documentNote.getId());
            if (note.getLemma() != null) {
                writer.element(SPAN, CLASS, "lemma");
                writer.write(note.getLemma());
                writer.end();
            }
            if (note.getTerm() != null && note.getTerm().getBasicForm() != null) {
                writer.element(SPAN, CLASS, "basicForm");
                writer.write(note.getTerm().getBasicForm());
                writer.end();
            }
            writer.end();

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
                    writer.element(SPAN);
                    writer.write("\u2013");
                    writer.end();
                }
                writeSpan(writer, "description");
                writeParagraph(writer, ParagraphParser.parseSafe(note.getDescription()));
                writer.end();
            }
            if (note.getSources() != null) {
                writeSpan(writer, "sources");
                writer.write("(");
                writeParagraph(writer, ParagraphParser.parseSafe(note.getSources()));
                writer.write(")");
                writer.end();
            }
            writer.end();
        }
        writer.end();
    }

    private void writeParagraph(MarkupWriter writer, Paragraph paragraph) {
        StringBuilder builder = new StringBuilder();
        for (ParagraphElement element : paragraph.getElements()) {
            if (element instanceof LinkElement) {
                LinkElement linkElement = (LinkElement) element;
                String reference = StringEscapeUtils.escapeHtml(linkElement.getReference());
                String string = StringEscapeUtils.escapeHtml(linkElement.getString());
                String result = "<a"
                        + (reference == null ? "" : " href=\"" + bibliographUrl + reference + "\"")
                        + ">" + string + "</a>";
                builder.append(result);
            } else if (element instanceof UrlElement) {
                UrlElement urlElement = (UrlElement) element;
                String url = StringEscapeUtils.escapeHtml(urlElement.getUrl());
                String string = StringEscapeUtils.escapeHtml(urlElement.getString());
                String result = "<a" + (url == null ? "" : " href=\"" + url + "\"") + ">" + string
                        + "</a>";
                builder.append(result);
            } else {
                builder.append(element.toString());
            }
        }
        writer.writeRaw(builder.toString());
    }

    @Override
    public void renderPageLinks(Document document, MarkupWriter writer) throws IOException,
            XMLStreamException {
        InputStream is = documentDao.getDocumentStream(document);
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
    public void renderDocument(Document document, MarkupWriter writer) throws IOException,
            XMLStreamException {
        renderDocument(document, null, writer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void renderDocumentAsXML(Document document, List<DocumentNote> documentNotes, OutputStream out) throws IOException, XMLStreamException{
        InputStream is = documentDao.getDocumentStream(document);
        XMLEventReader reader = inFactory.createXMLEventReader(is);
        XMLEventWriter writer = outFactory.createXMLEventWriter(out);

        QName note = new QName("note");
        XMLEvent openAnchor = null;
        try {
            while (reader.hasNext()){
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()){
                    StartElement startElement = event.asStartElement();
                    Iterator<Attribute> attributes = startElement.getAttributes();
                    if (startElement.getName().getLocalPart().equals(ANCHOR) && attributes.next().getValue().startsWith(END)){
                        String id = ((Iterator<Attribute>)startElement.getAttributes()).next().getValue().substring(3);
                        List<Attribute> atts = new ArrayList<Attribute>();
                        atts.add(eventFactory.createAttribute(TYPE, "editor"));
                        atts.add(eventFactory.createAttribute("xml:id", END+id));
                        atts.add(eventFactory.createAttribute("target", "#start"+id));
                        event = eventFactory.createStartElement(note, atts.iterator(), null);
                        openAnchor = event;
                    }else{
                        openAnchor = null;
                    }
                }else if (event.isEndElement()){
                    EndElement endElement = event.asEndElement();
                    if (openAnchor != null && endElement.getName().getLocalPart().equals(ANCHOR)){
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
            MarkupWriter writer) throws IOException, XMLStreamException {
        Set<Long> publishIds = null;
        if (documentNotes != null) {
            publishIds = new HashSet<Long>();
            for (DocumentNote documentNote : documentNotes) {
                publishIds.add(documentNote.getId());
            }
        }
        InputStream is = documentDao.getDocumentStream(document);
        XMLStreamReader reader = inFactory.createXMLStreamReader(is);

        MutableBoolean noteContent = new MutableBoolean(false);
        Set<Long> noteIds = new HashSet<Long>();
        ElementContext context = new ElementContext(3);

        try {
            while (true) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    handleStartElement(reader, writer, context, noteIds, noteContent, publishIds);
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
            ElementContext context, Set<Long> noteIds, MutableBoolean noteContent,
            Set<Long> publishIds) {
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
            String type = reader.getAttributeValue(null, TYPE);
            writer.element(localName, CLASS, type);
            if (path != null) {
                writer.attributes("id", path);
            }
        } else if (localName.equals("TEI") || localName.equals("TEI.2")) {
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
        } else if (localName.equals(ANCHOR)) {
            String id = reader.getAttributeValue(XML_NS, "id");
            if (id == null) {
                return;
            } else if (id.startsWith("start")) {
                if (publishIds != null && !publishIds.contains(Long.parseLong(id.replace("start", "")))) {
                    return;
                }
                // start anchor
                writer.element(SPAN, CLASS, "notestart", "id", id);
                writer.end();

                noteContent.setValue(true);
                noteIds.add(Long.parseLong(id.substring("start".length())));
            } else if (id.startsWith(END)) {                
                writer.element(SPAN, "class", "noteanchor", "id", id);
                writer.write(" [*] ");
                writer.end();
                
                noteIds.remove(Long.parseLong(id.substring(END.length())));
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
            return reader.getAttributeValue(null, TYPE);
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
            Set<Long> noteIds, MutableBoolean noteContent) {
        String text = WHITESPACE.matcher(reader.getText()).replaceAll(" ");
        if (noteContent.booleanValue() && !text.trim().isEmpty()) {
            StringBuilder classes = new StringBuilder("notecontent");
            for (Long noteId : noteIds) {
                classes.append(" n").append(noteId);
            }
            writer.element(SPAN, CLASS, classes);
            writer.writeRaw(StringEscapeUtils.escapeXml(text));
            writer.end();
        } else {
            writer.writeRaw(StringEscapeUtils.escapeXml(text));
        }
    }

}
