/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.mysema.edith.domain.*;
import com.mysema.edith.util.ParagraphParser;


public class ContentRendererTest extends AbstractHibernateTest {

    private static final String doc1 = "/documents/trunk/Nummisuutarit rakenteistettuna.xml";

    private static final String doc2 = "/documents/trunk/Nummisuutarit rakenteistettuna-annotoituna.xml";

    private static boolean initialized;
    
    @Inject
    private ContentRenderer renderer;

    @Inject
    private SubversionService subversionService;
    
    @Inject
    private DocumentDao documentDao;
    
    private XMLOutputFactory factory = XMLOutputFactory.newInstance();

//    private final MarkupWriter writer = new MarkupWriterImpl();
    private StringWriter str = new StringWriter();
    
    private XMLStreamWriter writer;

    @Before
    public void setUp() {
        if (!initialized) {
            initialized = true;
            subversionService.initialize();
        }
    }
    
    public ContentRendererTest() throws XMLStreamException {
        writer = factory.createXMLStreamWriter(str);
    }
        
    @Test
    public void Render_Document() throws Exception {
        renderer.renderDocument(documentDao.getDocumentForPath(doc1), writer);
    }

    @Test
    public void Render_Document_As_XML() throws IOException, XMLStreamException{
        List<DocumentNote> docNotes = Arrays.asList(createDocumentNote(NoteFormat.NOTE, true), 
                createDocumentNote(NoteFormat.PERSON, true));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        renderer.renderDocumentAsXML(documentDao.getDocumentForPath(doc2), docNotes, out);
    }

    @Test
    public void Render_PageLinks() throws Exception {
        renderer.renderPageLinks(documentDao.getDocumentForPath(doc1), writer);
    }

    @Test
    public void Render_Document_With_Notes() throws Exception {
        renderer.renderDocument(documentDao.getDocumentForPath(doc2), writer);
    }

    @Test
    public void Render_DocumentNotes_As_XML() throws XMLStreamException{
        List<DocumentNote> docNotes = Arrays.asList(createDocumentNote(NoteFormat.NOTE, true), 
                createDocumentNote(NoteFormat.PERSON, true));
        renderer.renderDocumentNotesAsXML(documentDao.getDocumentForPath(doc2), docNotes, writer);
    }

    private DocumentNote createDocumentNote(NoteFormat noteFormat, boolean extended) {
        Note note = new Note();

        note.setLemma("taloon");
        note.setLemmaMeaning("johonkin ineen");
        Paragraph paragraph = new Paragraph();
        LinkElement element = new LinkElement("Kalevala");
        element.setReference("kalevala");
        paragraph.addElement(element);
        note.setSubtextSources(paragraph.toString());
        note.setDescription(paragraph.toString());
        note.setSources(paragraph.toString());
        note.setFormat(noteFormat);
        if (extended) {
            note.setPerson(createPerson());
            note.setPlace(createPlace());
            note.setTerm(createTerm());
        }
        note.setTypes(Collections.singleton(NoteType.HISTORICAL));
        DocumentNote documentNote = new DocumentNote();
        documentNote.setNote(note);
        documentNote.setId(1234L);
        return documentNote;
    }

    private Term createTerm() {
        Term term = new Term();
        term.setBasicForm("basic form");
        return term;
    }

    private Place createPlace() {
        Place place = new Place();
        place.setNormalized(new NameForm("MyPlace", ""));
        place.setOtherForms(Collections.singleton(new NameForm("MyPlace", "")));
        return place;
    }

    private Person createPerson() {
        Person person = new Person();
        person.setNormalized(new NameForm("MyPlace", ""));
        person.setOtherForms(Collections.singleton(new NameForm("MyPlace", "")));
        return person;        
    }

    @Test
    public void Render_Normal_Note() throws XMLStreamException {
        List<DocumentNote> documentNotes = new ArrayList<DocumentNote>();
        documentNotes.add(createDocumentNote(NoteFormat.NOTE, false));
        renderer.renderDocumentNotes(documentNotes, writer);
        String document = str.toString();
        assertTrue(document.startsWith("<ul class=\"notes\">"));
        assertTrue(document
                .contains("<li><a href=\"#start1234\" class=\"notelink\"><span class=\"lemma\">taloon</span></a>"));
        assertTrue(document.contains("'johonkin ineen', Vrt. <a href=\"http://www.google.com/kalevala\">Kalevala</a>"));
        assertTrue(document.contains("<a href=\"http://www.google.com/kalevala\">Kalevala</a>"));
        assertTrue(document, document
                .contains("(<a href=\"http://www.google.com/kalevala\">Kalevala</a>)</span></li>"));
        assertTrue(document.endsWith("</ul>"));
    }

    @Test
    public void Render_Person_Note() throws XMLStreamException {
        List<DocumentNote> documentNotes = new ArrayList<DocumentNote>();
        DocumentNote documentNote = createDocumentNote(NoteFormat.PERSON, false);
        Person person = new Person(new NameForm("Fred", "Armisen", null), new HashSet<NameForm>());
        person.setTimeOfBirth(Interval.createYear(1970));
        person.setTimeOfDeath(Interval.createYear(2098));
        documentNote.getNote().setPerson(person);
        documentNotes.add(documentNote);
        renderer.renderDocumentNotes(documentNotes, writer);
        String document = str.toString();
        assertTrue(document.startsWith("<ul class=\"notes\">"));
        assertTrue(document
                .contains("<li><a href=\"#start1234\" class=\"notelink\"><span class=\"lemma\">taloon</span></a>"));
        assertTrue(document.contains("Fred Armisen"));
        assertTrue(document.contains("1970\u20132098."));
        assertTrue(document.contains("<span>\u2013</span>"));
        assertTrue(document.contains("<a href=\"http://www.google.com/kalevala\">Kalevala</a>"));
        assertTrue(document
                .contains("(<a href=\"http://www.google.com/kalevala\">Kalevala</a>)</span></li>"));
        assertTrue(document.endsWith("</ul>"));
    }

    @Test
    public void Render_Place_Note() throws XMLStreamException {
        List<DocumentNote> documentNotes = new ArrayList<DocumentNote>();
        DocumentNote documentNote = createDocumentNote(NoteFormat.PLACE, false);

        Paragraph description = ParagraphParser.parseSafe(documentNote.getNote().getDescription());
        description.addElement(new StringElement(" foo "));
        UrlElement urlElement = new UrlElement("Google");
        urlElement.setUrl("http://www.google.com/");
        description.addElement(urlElement);
        description.addElement(new UrlElement("happyness"));
        description.addElement(new LinkElement("maya"));
        documentNote.getNote().setDescription(description.toString());
        Place place = new Place(new NameForm("New York", null), new HashSet<NameForm>());
        documentNote.getNote().setPlace(place);
        documentNotes.add(documentNote);
        renderer.renderDocumentNotes(documentNotes, writer);
        String document = str.toString();
//        System.err.println(document);
        assertTrue(document.startsWith("<ul class=\"notes\">"));
        assertTrue(document
                .contains("<li><a href=\"#start1234\" class=\"notelink\"><span class=\"lemma\">taloon</span></a>"));
        assertTrue(document.contains("New York"));
        assertTrue(document.contains("<span>\u2013</span>"));
        assertTrue(document
                .contains("<a href=\"http://www.google.com/kalevala\">Kalevala</a> foo <a href=\"http://www.google.com/\">Google</a>"));
        assertTrue(document.contains("<a>happyness</a>"));
        assertTrue(document.contains("<a>maya</a>"));
        assertTrue(document
                .contains("(<a href=\"http://www.google.com/kalevala\">Kalevala</a>)</span></li>"));
        assertTrue(document.endsWith("</ul>"));
    }
}
