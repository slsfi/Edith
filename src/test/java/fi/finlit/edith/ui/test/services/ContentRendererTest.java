/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.internal.services.MarkupWriterImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.Interval;
import fi.finlit.edith.domain.LinkElement;
import fi.finlit.edith.domain.NameForm;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteFormat;
import fi.finlit.edith.domain.Paragraph;
import fi.finlit.edith.domain.Person;
import fi.finlit.edith.domain.Place;
import fi.finlit.edith.ui.services.ContentRenderer;

/**
 * TEITest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class ContentRendererTest extends AbstractServiceTest {

    private static final String doc1 = "/documents/trunk/Nummisuutarit rakenteistettuna.xml";

    private static final String doc2 = "/documents/trunk/Nummisuutarit rakenteistettuna-annotoituna.xml";

    @Inject
    private ContentRenderer renderer;

    @Inject
    private DocumentRepository documentRepository;

    private final MarkupWriter writer = new MarkupWriterImpl();

    @Test
    public void renderDocument() throws Exception {
        renderer.renderDocument(documentRepository.getDocumentForPath(doc1).getRevision(-1), writer);
    }

    @Test
    public void renderPageLinks() throws Exception {
        renderer.renderPageLinks(documentRepository.getDocumentForPath(doc1).getRevision(-1),
                writer);
    }

    @Test
    public void renderDocumentWithNotes() throws Exception {
        renderer.renderDocument(documentRepository.getDocumentForPath(doc2).getRevision(-1), writer);
    }

    private DocumentNote createDocumentNote(NoteFormat noteFormat) {
        Note note = new Note();
        note.setLemma("taloon");
        note.setLemmaMeaning("johonkin ineen");
        Paragraph paragraph = new Paragraph();
        LinkElement element = new LinkElement("Kalevala");
        element.setReference("kalevala");
        paragraph.addElement(element);
        note.setSubtextSources(paragraph);
        note.setDescription(paragraph);
        note.setSources(paragraph);
        note.setFormat(noteFormat);
        DocumentNote documentNote = new DocumentNote();
        documentNote.setNote(note);
        documentNote.setLocalId("1234");
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
        assertTrue(document.contains(", 'johonkin ineen'"));
        assertTrue(document.contains(", Vrt. <a href=\"kalevala\">Kalevala</a>"));
        assertTrue(document.contains(", <a href=\"kalevala\">Kalevala</a>"));
        assertTrue(document.contains(", (<a href=\"kalevala\">Kalevala</a>)</li>"));
        assertTrue(document.endsWith("</ul>"));
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
        assertTrue(document.contains(", Fred"));
        assertTrue(document.contains(", Armisen"));
        assertTrue(document.contains(", 1970\u20132098."));
        assertTrue(document.contains(", <a href=\"kalevala\">Kalevala</a>"));
        assertTrue(document.contains(", (<a href=\"kalevala\">Kalevala</a>)</li>"));
        assertTrue(document.endsWith("</ul>"));
    }

    @Test
    public void Render_Place_Note() {
        List<DocumentNote> documentNotes = new ArrayList<DocumentNote>();
        DocumentNote documentNote = createDocumentNote(NoteFormat.PLACE);
        Place place = new Place(new NameForm("New York", null), new HashSet<NameForm>());
        documentNote.getNote().setPlace(place);
        documentNotes.add(documentNote);
        renderer.renderDocumentNotes(documentNotes, writer);
        String document = writer.toString();
        assertTrue(document.startsWith("<ul class=\"notes\">"));
        assertTrue(document
                .contains("<li><a href=\"#start1234\" class=\"notelink\"><em>taloon</em></a>"));
        assertTrue(document.contains(", New York"));
        assertTrue(document.contains(", <a href=\"kalevala\">Kalevala</a>"));
        assertTrue(document.contains(", (<a href=\"kalevala\">Kalevala</a>)</li>"));
        assertTrue(document.endsWith("</ul>"));
    }
}
