/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.Transactional;
import com.mysema.edith.EdithTestConstants;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Interval;
import com.mysema.edith.domain.NameForm;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.NoteFormat;
import com.mysema.edith.domain.NoteType;
import com.mysema.edith.domain.Person;
import com.mysema.edith.domain.Place;
import com.mysema.edith.domain.Term;
import com.mysema.edith.domain.User;
import com.mysema.edith.dto.DocumentTO;
import com.mysema.edith.dto.NoteSearchTO;

@Transactional
public class DocumentNoteDaoTest extends AbstractHibernateTest {

    @Inject @Named(EdithTestConstants.TEST_DOCUMENT_KEY)
    private String testDocument;

    @Inject
    private UserDao userDao;

    @Inject
    private NoteDao noteDao;

    @Inject
    private DocumentNoteDao documentNoteDao;

    @Inject
    private DocumentDao documentDao;
    
    private Document document;

    private NoteSearchTO searchInfo;

    private DocumentNote documentNote1;

    private DocumentNote documentNote2;

    private DocumentNote documentNote3;

    private DocumentNote documentNote4;

    @Inject
    private SubversionService versioningService;

    private boolean initialized = false;

    @Before
    public void setUp() {
        if (userDao.getAll().isEmpty()) {
            userDao.save(new User("timo"));
        }
        document = documentDao.getDocumentForPath(testDocument);

        documentNote1 = noteDao.createDocumentNote(createNote(), document, "l\u00E4htee h\u00E4ihins\u00E4 Mikko Vilkastuksen");
        documentNote2 = noteDao.createDocumentNote(createNote(), document, "koska suutarille k\u00E4skyn k\u00E4r\u00E4jiin annoit, saadaksesi naimalupaa.");
        documentNote3 = noteDao.createDocumentNote(createNote(), document, "tulee, niin seisoo s\u00E4\u00E4t\u00F6s-kirjassa.");
        documentNote4 = noteDao.createDocumentNote(createNote(), document, "kummallenkin m\u00E4\u00E4r\u00E4tty, niin emmep\u00E4 tiet\u00E4isi t\u00E4ss\u00E4");

        DocumentTO documentTO = new DocumentTO();
        documentTO.setId(document.getId());
        documentTO.setPath(document.getPath());
        
        searchInfo = new NoteSearchTO();
        searchInfo.setCurrentDocument(documentTO);

        addExtraNote("testo");
        addExtraNote("testo2");

        if (!initialized) {
            versioningService.initialize();
            initialized = true;
        }
    }

    @Test
    public void GetBy_LocalId_Returns_NonNull_Result() {
        assertNotNull(documentNoteDao.getById(documentNote1.getId()));
        assertNotNull(documentNoteDao.getById(documentNote2.getId()));
        assertNotNull(documentNoteDao.getById(documentNote3.getId()));
        assertNotNull(documentNoteDao.getById(documentNote4.getId()));
    }

    @Test
    public void Get_Of_Document_Resturns_Right_Amount_Of_Results() {
        assertEquals(4, documentNoteDao.getOfDocument(document).size());
    }

    @Test
    public void Get_Of_Document_With_Note_Updates() {
        assertEquals(4, documentNoteDao.getOfDocument(document).size());

        for (DocumentNote documentNote : documentNoteDao.getOfDocument(document)) {
            documentNote.getNote().setLemma(documentNote.getNote().getLemma() + "XXX");
            documentNoteDao.save(documentNote);
        }

        assertEquals(4, documentNoteDao.getOfDocument(document).size());
    }

    @Test
    public void Remove_Sets_Deleted_Flag() {
        DocumentNote documentNote = documentNoteDao.getById(documentNote1.getId());
        documentNoteDao.remove(documentNote);
        assertTrue(documentNoteDao.getById(documentNote.getId()).isDeleted());
    }

    @Test
    public void Remove_By_Id() {
        DocumentNote documentNote = documentNoteDao.getById(documentNote1.getId());
        documentNoteDao.remove(documentNote);
        assertTrue(documentNoteDao.getById(documentNote.getId()).isDeleted());
    }

    @Test
    public void Store_And_Retrieve_Person_Note() {
        DocumentNote documentNote = noteDao
                .createDocumentNote(createNote(), document,
                        "kummallenkin m\u00E4\u00E4r\u00E4tty, niin emmep\u00E4 tiet\u00E4isi t\u00E4ss\u00E4");
        Note note = documentNote.getNote();
        note.setFormat(NoteFormat.PERSON);
        NameForm normalizedForm = new NameForm("Aleksis", "Kivi",
                "Suomen hienoin kirjailija ikinä.");
        Set<NameForm> otherForms = new HashSet<NameForm>();
        otherForms.add(new NameForm("Alexis", "Stenvall", "En jättebra skrivare."));
        note.setPerson(new Person(normalizedForm, otherForms));
        Interval timeOfBirth = Interval.createDate(new LocalDate(1834, 10, 10));
        Interval timeOfDeath = Interval.createDate(new LocalDate(1872, 12, 31));
        note.getPerson().setTimeOfBirth(timeOfBirth);
        note.getPerson().setTimeOfDeath(timeOfDeath);
        noteDao.save(note);
        Note persistedNote = documentNoteDao.getById(documentNote.getId()).getNote();
        assertEquals(note.getPerson().getNormalized().getName(), persistedNote.getPerson()
                .getNormalized().getName());
        assertEquals(note.getPerson().getNormalized().getDescription(), persistedNote
                .getPerson().getNormalized().getDescription());
        assertEquals(note.getFormat(), persistedNote.getFormat());

        assertEquals(note.getPerson().getTimeOfBirth().getStart(), persistedNote.getPerson().getTimeOfBirth().getStart());
        assertEquals(note.getPerson().getTimeOfBirth().getEnd(),   persistedNote.getPerson().getTimeOfBirth().getEnd());
        assertEquals(note.getPerson().getTimeOfBirth().getDate(), persistedNote.getPerson().getTimeOfBirth().getDate());

        assertEquals(note.getPerson().getTimeOfDeath().getStart(), persistedNote.getPerson().getTimeOfDeath().getStart());
        assertEquals(note.getPerson().getTimeOfDeath().getEnd(), persistedNote.getPerson().getTimeOfDeath().getEnd());
        assertEquals(note.getPerson().getTimeOfDeath().getDate(), persistedNote.getPerson().getTimeOfDeath().getDate());
    }

    @Test
    public void Store_And_Retrieve_Person_With_The_Same_Birth_And_Death_Date() {
        DocumentNote documentNote = noteDao
                .createDocumentNote(createNote(), document,
                        "kummallenkin m\u00E4\u00E4r\u00E4tty, niin emmep\u00E4 tiet\u00E4isi t\u00E4ss\u00E4");
        Note note = documentNote.getNote();
        note.setFormat(NoteFormat.PERSON);
        Interval timeOfBirth = Interval.createYear(1834);
        Interval timeOfDeath = Interval.createYear(1834);
        note.setPerson(new Person());
        note.getPerson().setTimeOfBirth(timeOfBirth);
        note.getPerson().setTimeOfDeath(timeOfDeath);
        noteDao.save(note);
        Note persistedNote = documentNoteDao.getById(documentNote.getId()).getNote();
        assertNotNull(persistedNote.getPerson().getTimeOfBirth());
        assertNotNull(persistedNote.getPerson().getTimeOfDeath());
    }

    @Test
    public void Store_And_Retrieve_Place_Note() {
        DocumentNote documentNote = noteDao
                .createDocumentNote(createNote(), document,
                        "kummallenkin m\u00E4\u00E4r\u00E4tty, niin emmep\u00E4 tiet\u00E4isi t\u00E4ss\u00E4");
        Note note = documentNote.getNote();
        note.setFormat(NoteFormat.PLACE);
        NameForm normalizedForm = new NameForm("Tampere", "Kaupunki Hämeessä.");
        Set<NameForm> otherForms = new HashSet<NameForm>();
        otherForms.add(new NameForm("Tammerfors", "Ruotsinkielinen nimitys."));
        note.setPlace(new Place(normalizedForm, otherForms));
        noteDao.save(note);
        Note persistedNote = documentNoteDao.getById(documentNote.getId()).getNote();
        assertEquals(note.getPlace().getNormalized().getName(), persistedNote.getPlace()
                .getNormalized().getName());
        assertEquals(note.getPlace().getNormalized().getDescription(), persistedNote.getPlace()
                .getNormalized().getDescription());
        assertEquals(note.getFormat(), persistedNote.getFormat());
    }

    @Test
    public void Get_Document_Notes_Of_Note() {
        List<DocumentNote> documentNotesOfDocument = documentNoteDao.getOfDocument(document);
        assertFalse(documentNotesOfDocument.isEmpty());
        List<DocumentNote> documentNotesOfNote = documentNoteDao
                .getOfNote(documentNotesOfDocument.get(0).getNote().getId());
        assertFalse(documentNotesOfNote.isEmpty());
    }

    @Test
    public void Get_Document_Notes_Of_Term() {
        List<DocumentNote> documentNotesOfDocument = documentNoteDao.getOfDocument(document);
        Term term = new Term();
        term.setBasicForm("foobar");
        term.setMeaning("a placeholder");
        DocumentNote documentNote = documentNotesOfDocument.get(0);
        documentNote.getNote().setTerm(term);
        noteDao.save(documentNote.getNote());

        List<DocumentNote> documentNotesOfTerm = documentNoteDao.getOfTerm(documentNote.getNote().getTerm().getId());
        assertEquals(documentNote, documentNotesOfTerm.get(0));
    }

    @Test
    public void Add_The_Same_Word_Twice() {
        String text = "l\u00E4htee";
        Note note = noteDao.createDocumentNote(createNote(), document, text).getNote();
        noteDao.createDocumentNote(note, document, text);
//        assertEquals(6, countDocumentNotes(noteDao.listNotes(searchInfo))); // FIXME
    }

    @Test
    public void Get_Document_Notes_Of_Person() {
        DocumentNote documentNote = documentNoteDao.getOfDocument(document).iterator().next();
        Person person = new Person(new NameForm("Tom", "Sawyer"), new HashSet<NameForm>());
        documentNote.getNote().setPerson(person);
        documentNoteDao.save(documentNote);
        assertEquals(1, documentNoteDao.getOfPerson(person.getId()).size());
    }

    @Test
    public void Get_Document_Notes_Of_Place() {
        DocumentNote documentNote = documentNoteDao.getOfDocument(document).iterator().next();
        Place place = new Place(new NameForm("Helsinki", "Capital of Finland"),
                new HashSet<NameForm>());
        documentNote.getNote().setPlace(place);
        documentNoteDao.save(documentNote);
        assertEquals(1, documentNoteDao.getOfPlace(place.getId()).size());
    }

    @Test
    public void GetPublishableNotesOfDocument() {
        assertEquals(0, documentNoteDao.getPublishableNotesOfDocument(document).size());
    }

    private void addExtraNote(String username) {
        DocumentNote documentNote = new DocumentNote();
        User user = userDao.getByUsername(username);
        if (user == null) {
            user = new User(username);
            userDao.save(user);
        }
        Note note = createNote();
        note.setLemma("TheLemma");
        note.setTypes(new HashSet<NoteType>());
        note.getTypes().add(NoteType.HISTORICAL);
        note.setFormat(NoteFormat.PERSON);
        note.setLastEditedBy(user);
        note.addEditor(user);
        noteDao.save(note);
        documentNote.setNote(note);
        documentNote.setFullSelection("thelongtext");
        documentNote.setCreatedOn(new DateTime().getMillis());
        documentNoteDao.save(documentNote);
    }

    private Note createNote() {
        Note note = new Note();
        note.setTerm(new Term());
        return note;
    }
}
