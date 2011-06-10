/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.internal.services.MarkupWriterImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.Test;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.*;
import fi.finlit.edith.ui.services.ContentRenderer;
import fi.finlit.edith.ui.services.DocumentRepository;

public class ContentRendererTest extends AbstractServiceTest {

    private static final String doc1 = "/documents/trunk/Nummisuutarit rakenteistettuna.xml";

    private static final String doc2 = "/documents/trunk/Nummisuutarit rakenteistettuna-annotoituna.xml";

    @Inject
    private ContentRenderer renderer;

    @Inject
    private DocumentRepository documentRepository;

    @Inject @Symbol(EDITH.EXTENDED_TERM)
    private boolean extendedTerm;

    private final MarkupWriter writer = new MarkupWriterImpl();

    @Test
    public void renderDocument() throws Exception {
        renderer.renderDocument(documentRepository.getOrCreateDocumentForPath(doc1).getRevision(-1), writer);
    }

    @Test
    public void renderDocumentAsXML() throws IOException, XMLStreamException{
        List<DocumentNote> docNotes = Arrays.asList(createDocumentNote(NoteFormat.NOTE), createDocumentNote(NoteFormat.PERSON));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        renderer.renderDocumentAsXML(documentRepository.getOrCreateDocumentForPath(doc2).getRevision(-1), docNotes, out);
    }

    @Test
    public void renderPageLinks() throws Exception {
        renderer.renderPageLinks(documentRepository.getOrCreateDocumentForPath(doc1).getRevision(-1),
                writer);
    }

    @Test
    public void renderDocumentWithNotes() throws Exception {
        renderer.renderDocument(documentRepository.getOrCreateDocumentForPath(doc2).getRevision(-1), writer);
    }

    @Test
    public void renderDocumentNotesAsXML(){
        List<DocumentNote> docNotes = Arrays.asList(createDocumentNote(NoteFormat.NOTE), createDocumentNote(NoteFormat.PERSON));
        renderer.renderDocumentNotesAsXML(documentRepository.getOrCreateDocumentForPath(doc2).getRevision(-1), docNotes, writer);
    }

    private DocumentNote createDocumentNote(NoteFormat noteFormat) {
        Note note = new Note();
        Concept concept = note.getConcept(extendedTerm);

        note.setLemma("taloon");
        note.setLemmaMeaning("johonkin ineen");
        Paragraph paragraph = new Paragraph();
        LinkElement element = new LinkElement("Kalevala");
        element.setReference("kalevala");
        paragraph.addElement(element);
        concept.setSubtextSources(paragraph.toString());
        concept.setDescription(paragraph.copy().toString());
        concept.setSources(paragraph.copy().toString());
        note.setFormat(noteFormat);
        DocumentNote documentNote = new DocumentNote();
        documentNote.setNote(note);
        documentNote.setId("1234");
        return documentNote;
    }

    @Test
    public void Render_Normal_Note() {
        List<DocumentNote> documentNotes = new ArrayList<DocumentNote>();
        documentNotes.add(createDocumentNote(NoteFormat.NOTE));
        renderer.renderDocumentNotes(documentNotes, writer);
        String document = writer.toString();
        assertTrue(document.startsWith("<ul class=\"notes\">"));
        assertTrue(document
                .contains("<li><a href=\"#start1234\" class=\"notelink\"><em>taloon</em></a>"));
        assertTrue(document.contains("'johonkin ineen', Vrt. <a href=\"http://www.google.com/kalevala\">Kalevala</a>"));
        assertTrue(document.contains("<a href=\"http://www.google.com/kalevala\">Kalevala</a>"));
        assertTrue(document, document
                .contains("(<a href=\"http://www.google.com/kalevala\">Kalevala</a>)</SPAN></li>"));
        assertTrue(document.endsWith("</ul>"));
        System.err.println(document);
    }

    @Test
    public void Render_Person_Note() {
        List<DocumentNote> documentNotes = new ArrayList<DocumentNote>();
        DocumentNote documentNote = createDocumentNote(NoteFormat.PERSON);
        Person person = new Person(new NameForm("Fred", "Armisen", null), new HashSet<NameForm>());
        person.setTimeOfBirth(Interval.createYear(1970));
        person.setTimeOfDeath(Interval.createYear(2098));
        documentNote.getNote().setPerson(person);
        documentNotes.add(documentNote);
        renderer.renderDocumentNotes(documentNotes, writer);
        String document = writer.toString();
        assertTrue(document.startsWith("<ul class=\"notes\">"));
        assertTrue(document
                .contains("<li><a href=\"#start1234\" class=\"notelink\"><em>taloon</em></a>"));
        assertTrue(document.contains("Fred Armisen"));
        assertTrue(document.contains("1970\u20132098."));
        assertTrue(document.contains("<span>\u2013</span>"));
        assertTrue(document.contains("<a href=\"http://www.google.com/kalevala\">Kalevala</a>"));
        assertTrue(document
                .contains("(<a href=\"http://www.google.com/kalevala\">Kalevala</a>)</SPAN></li>"));
        assertTrue(document.endsWith("</ul>"));
    }

    @Test
    public void Render_Place_Note() {
        List<DocumentNote> documentNotes = new ArrayList<DocumentNote>();
        DocumentNote documentNote = createDocumentNote(NoteFormat.PLACE);
        Concept concept = documentNote.getConcept(extendedTerm);

        Paragraph description = Paragraph.parseSafe(concept.getDescription());
        description.addElement(new StringElement(" foo "));
        UrlElement urlElement = new UrlElement("Google");
        urlElement.setUrl("http://www.google.com/");
        description.addElement(urlElement);
        description.addElement(new UrlElement("happyness"));
        description.addElement(new LinkElement("maya"));
        concept.setDescription(description.toString());
        Place place = new Place(new NameForm("New York", null), new HashSet<NameForm>());
        documentNote.getNote().setPlace(place);
        documentNotes.add(documentNote);
        renderer.renderDocumentNotes(documentNotes, writer);
        String document = writer.toString();
//        System.err.println(document);
        assertTrue(document.startsWith("<ul class=\"notes\">"));
        assertTrue(document
                .contains("<li><a href=\"#start1234\" class=\"notelink\"><em>taloon</em></a>"));
        assertTrue(document.contains("New York"));
        assertTrue(document.contains("<span>\u2013</span>"));
        assertTrue(document
                .contains("<a href=\"http://www.google.com/kalevala\">Kalevala</a> foo <a href=\"http://www.google.com/\">Google</a>"));
        assertTrue(document.contains("<a>happyness</a>"));
        assertTrue(document.contains("<a>maya</a>"));
        assertTrue(document
                .contains("(<a href=\"http://www.google.com/kalevala\">Kalevala</a>)</SPAN></li>"));
        assertTrue(document.endsWith("</ul>"));
    }
}
