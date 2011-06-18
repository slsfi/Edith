/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.hibernate;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.grid.ColumnSort;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fi.finlit.edith.EdithTestConstants;
import fi.finlit.edith.dto.NoteSearchInfo;
import fi.finlit.edith.dto.OrderBy;
import fi.finlit.edith.dto.UserInfo;
import fi.finlit.edith.sql.domain.Document;
import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.sql.domain.Interval;
import fi.finlit.edith.sql.domain.NameForm;
import fi.finlit.edith.sql.domain.Note;
import fi.finlit.edith.sql.domain.NoteFormat;
import fi.finlit.edith.sql.domain.NoteStatus;
import fi.finlit.edith.sql.domain.NoteType;
import fi.finlit.edith.sql.domain.Person;
import fi.finlit.edith.sql.domain.Place;
import fi.finlit.edith.sql.domain.Term;
import fi.finlit.edith.sql.domain.User;
import fi.finlit.edith.ui.services.DocumentDao;
import fi.finlit.edith.ui.services.DocumentNoteDao;
import fi.finlit.edith.ui.services.NoteDao;
import fi.finlit.edith.ui.services.UserDao;

public class DocumentNoteDaoTest extends AbstractHibernateTest {
    @Inject
    @Symbol(EdithTestConstants.TEST_DOCUMENT_KEY)
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

    private NoteSearchInfo searchInfo;

    private DocumentNote documentNote1;

    private DocumentNote documentNote2;

    private DocumentNote documentNote3;

    private DocumentNote documentNote4;

    @Inject
    @Symbol(EdithTestConstants.NOTE_TEST_DATA_KEY)
    private File noteTestData;


    private int countDocumentNotes(List<Note> notes){
        int count = 0;
        for (Note n : notes){
            count += n.getDocumentNoteCount();
        }
        return count;
    }

    @Before
    public void setUp() {
        userDao.save(new User("timo"));
        document = documentDao.getOrCreateDocumentForPath(testDocument);
        
        documentNote1 = noteDao.createDocumentNote(createNote(), document, "l\u00E4htee h\u00E4ihins\u00E4 Mikko Vilkastuksen", 0);
        documentNote2 = noteDao.createDocumentNote(createNote(), document, "koska suutarille k\u00E4skyn k\u00E4r\u00E4jiin annoit, saadaksesi naimalupaa.", 0);
        documentNote3 = noteDao.createDocumentNote(createNote(), document, "tulee, niin seisoo s\u00E4\u00E4t\u00F6s-kirjassa.", 0);
        documentNote4 = noteDao.createDocumentNote(createNote(), document, "kummallenkin m\u00E4\u00E4r\u00E4tty, niin emmep\u00E4 tiet\u00E4isi t\u00E4ss\u00E4", 0);

        searchInfo = new NoteSearchInfo();
        searchInfo.setCurrentDocument(document);

        addExtraNote("testo");
        addExtraNote("testo2");
    }

    @Test
    public void GetByLocalId_Returns_NonNull_Result() {
        assertNotNull(documentNoteDao.getById(documentNote1.getId()));
        assertNotNull(documentNoteDao.getById(documentNote2.getId()));
        assertNotNull(documentNoteDao.getById(documentNote3.getId()));
        assertNotNull(documentNoteDao.getById(documentNote4.getId()));
    }

    @Test
    public void GetOfDocument_Resturns_Right_Amount_Of_Results() {
        assertEquals(4, documentNoteDao.getOfDocument(document).size());
    }

    @Test
    public void GetOfDocument_With_Note_Updates() {
        assertEquals(4, documentNoteDao.getOfDocument(document).size());

        for (DocumentNote documentNote : documentNoteDao.getOfDocument(document)) {
//            documentNote = documentNote.createCopy();
            documentNote.getNote().setLemma(documentNote.getNote().getLemma() + "XXX");
            documentNoteDao.save(documentNote);
        }

        assertEquals(4, documentNoteDao.getOfDocument(document).size());
    }

    @Test
    public void QueryNotes_Returns_More_Than_Zero_Results() {
        assertTrue(documentNoteDao.queryNotes("annoit").getAvailableRows() > 0);
    }

    @Test
    public void QueryNotes_Sorting_Is_Case_Insensitive() {
        noteDao.createDocumentNote(createNote(), document, "a");
        noteDao.createDocumentNote(createNote(), document, "b");
        noteDao.createDocumentNote(createNote(), document, "A");
        noteDao.createDocumentNote(createNote(), document, "B");
        GridDataSource gds = documentNoteDao.queryNotes("*");
        int n = gds.getAvailableRows();
        List<SortConstraint> sortConstraints = new ArrayList<SortConstraint>();
        sortConstraints.add(new SortConstraint(new PropertyModelMock(), ColumnSort.ASCENDING));
        gds.prepare(0, 100, sortConstraints);
        String previous = null;
        for (int i = 0; i < n; ++i) {
            DocumentNote dn = (DocumentNote) gds.getRowValue(i);
            String current = dn.getFullSelection().toLowerCase();
            if (previous != null) {
                assertThat("The actual value was probably in upper case!", previous,
                        lessThanOrEqualTo(current));
            }
            previous = current;
        }
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
        documentNoteDao.remove(documentNote.getId());
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
        assertEquals(note.getPerson().getNormalizedForm().getName(), persistedNote.getPerson()
                .getNormalizedForm().getName());
        assertEquals(note.getPerson().getNormalizedForm().getDescription(), persistedNote
                .getPerson().getNormalizedForm().getDescription());
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
        assertEquals(note.getPlace().getNormalizedForm().getName(), persistedNote.getPlace()
                .getNormalizedForm().getName());
        assertEquals(note.getPlace().getNormalizedForm().getDescription(), persistedNote.getPlace()
                .getNormalizedForm().getDescription());
        assertEquals(note.getFormat(), persistedNote.getFormat());
    }

    @Test
    public void Query_For_All_Notes() {
        List<Note> notes = noteDao.listNotes(searchInfo);
        assertEquals(6, notes.size());
    }

    @Test
    public void Query_and_Delete(){
        searchInfo.setDocuments(Collections.singleton(document));
        List<Note> notes = noteDao.listNotes(searchInfo);
        assertFalse(notes.isEmpty());

        // remove note
        assertTrue(notes.get(0).getDocumentNoteCount() == 1);
        DocumentNote documentNote = documentNoteDao.getOfNote(notes.get(0).getId()).get(0);
        documentDao.removeDocumentNotes(document, documentNote);

        // assert that query returns less results
        List<Note> newResults = noteDao.listNotes(searchInfo);
        assertEquals(notes.size() - 1, newResults.size());

    }

    @Test
    public void Query_For_All_Notes_Ordered_By_Lemma_Ascending_By_Default() {
        List<Note> notes = noteDao.listNotes(searchInfo);
        DocumentNote previous = null;
        for (Note note : notes){
            for (DocumentNote documentNote : documentNoteDao.getOfNote(note.getId())) {
                if (previous != null) {
                    assertThat(previous.getNote().getLemma().toLowerCase(),
                            lessThanOrEqualTo(documentNote.getNote().getLemma().toLowerCase()));
                }
                previous = documentNote;
            }
        }
    }

    @Test
    public void Query_For_Notes_Based_On_Document() {
        searchInfo.getDocuments().add(document);
        assertEquals(4, noteDao.listNotes(searchInfo).size());
    }

    @Test
    public void Query_For_Notes_Based_On_Creator() {
        UserInfo userInfo = userDao.getUserInfoByUsername("testo");
        searchInfo.getCreators().add(userInfo);
        assertEquals(1, noteDao.listNotes(searchInfo).size());
    }

    @Test
    @Ignore // FIXME
    public void Query_For_Notes_Based_On_Editors() {
//        UserInfo userInfo = userDao.getUserInfoByUsername("testo");
//        searchInfo.getCreators().add(userInfo);
//        List<Note> notes = noteDao.listNotes(searchInfo);
//        assertEquals(1, notes.size());
//        documentNoteDao.save(notes.get(0).getDocumentNotes().iterator().next());
//        notes = noteDao.listNotes(searchInfo);
//        assertEquals(1, notes.size());
//        assertEquals("timo", notes.get(0).getNote().getLastEditedBy().getUsername());
    }

    @Test
    public void Query_For_Notes_Based_On_Creators() {
        UserInfo userInfo1 = userDao.getUserInfoByUsername("testo");
        UserInfo userInfo2 = userDao.getUserInfoByUsername("testo2");
        searchInfo.getCreators().add(userInfo1);
        searchInfo.getCreators().add(userInfo2);
        assertEquals(2, noteDao.listNotes(searchInfo).size());
    }

    @Test
    //TODO This is not working for some reason, 
    public void Query_For_Notes_Based_On_Note_Type() {
        searchInfo.getNoteTypes().add(NoteType.HISTORICAL);
        assertEquals(2, noteDao.listNotes(searchInfo).size());
    }

    @Test
  //TODO This is not working for some reason
    public void Query_For_Notes_Based_On_Note_Type_Two_Filters() {
        searchInfo.getNoteTypes().add(NoteType.HISTORICAL);
        searchInfo.getNoteTypes().add(NoteType.DICTUM);
        assertEquals(2, noteDao.listNotes(searchInfo).size());
    }

    @Test
    public void Query_For_Notes_Based_On_Note_Format() {
        searchInfo.getNoteFormats().add(NoteFormat.PERSON);
        assertEquals(2, noteDao.listNotes(searchInfo).size());
    }

    @Test
    public void Query_For_All_Notes_Order_By_Creator_Ascending() {
        searchInfo.setOrderBy(OrderBy.USER);
        List<Note> notes = noteDao.listNotes(searchInfo);
        DocumentNote previous = null;
        for (Note note : notes){
            for (DocumentNote documentNote : documentNoteDao.getOfNote(note.getId())) {
                if (previous != null) {
                    String previousUsername, currentUsername;
                    previousUsername = previous.getNote().getLastEditedBy().getUsername();
                    currentUsername = documentNote.getNote().getLastEditedBy().getUsername();
                    assertThat(previousUsername, lessThanOrEqualTo(currentUsername));
                }
                previous = documentNote;
            }
        }
    }

    @Test
    public void Query_For_All_Notes_Order_By_Creator_Descending() {
        searchInfo.setOrderBy(OrderBy.USER);
        searchInfo.setAscending(false);
        List<Note> notes = noteDao.listNotes(searchInfo);
        DocumentNote previous = null;
        for (Note note : notes){
            for (DocumentNote documentNote : documentNoteDao.getOfNote(note.getId())) {
                if (previous != null) {
                    String previousUsername, currentUsername;
                    previousUsername = previous.getNote().getLastEditedBy().getUsername();
                    currentUsername = documentNote.getNote().getLastEditedBy().getUsername();
                    assertThat(previousUsername, greaterThanOrEqualTo(currentUsername));
                }
                previous = documentNote;
            }
        }
    }

    @Test
    public void Query_For_All_Notes_Order_By_Date_Of_Creation_Ascending() {
        searchInfo.setOrderBy(OrderBy.DATE);
        List<Note> notes = noteDao.listNotes(searchInfo);
        Note previous = null;
        for (Note note : notes){
            if (previous != null){
                assertThat(previous.getEditedOn(), lessThanOrEqualTo(note.getEditedOn()));
            }
            previous = note;
        }
    }

    @Test
    public void Query_For_Orphans() {
        noteDao.importNotes(noteTestData);
        assertEquals(15, noteDao.getAll().size());
        searchInfo.setOrphans(true);
        //assertEquals(9, noteDao.listNotes(searchInfo).size());
        //TODO Should it be 9??
        assertEquals(11, noteDao.listNotes(searchInfo).size());
    }

    @Test
    //TODO Fix this too
    public void Query_For_All_Notes_Order_By_Status() {
        searchInfo.setOrderBy(OrderBy.STATUS);
        List<Note> notes = noteDao.listNotes(searchInfo);
        DocumentNote edited = documentNoteDao.getOfNote(notes.get(2).getId()).get(0);
        edited.getNote().setStatus(NoteStatus.FINISHED);
        documentNoteDao.save(edited);
        notes = noteDao.listNotes(searchInfo);
        DocumentNote previous = null;
        for (Note note : notes){
            for (DocumentNote documentNote : documentNoteDao.getOfNote(note.getId())) {
                if (previous != null) {
                    NoteStatus previousStatus = previous.getNote().getStatus();
                    NoteStatus currentStatus = documentNote.getNote().getStatus();
                    assertTrue(previousStatus + " " + currentStatus,
                            previousStatus.compareTo(currentStatus) <= 0);
                }
                previous = documentNote;
            }
        }

    }

    //TODO Is this needed
//    @Test
//    public void Save_As_Copy() {
//        DocumentNote documentNote = documentNoteDao.getOfDocument(document).get(0);
//        Note initialNote = noteDao.getById(documentNote.getNote().getId());
//        Concept concept = initialNote.getConcept(extendedTerm);
//
//        concept.setDescription(new Paragraph().toString());
//        initialNote.setFormat(NoteFormat.PLACE);
//        initialNote.setLemmaMeaning("fajflkjsalj");
//        initialNote.setPerson(new Person(new NameForm(), new HashSet<NameForm>()));
//        initialNote.setPlace(new Place(new NameForm(), new HashSet<NameForm>()));
//        concept.setSources(new Paragraph().toString());
//        concept.setSubtextSources(new Paragraph().toString());
//        initialNote.setTerm(new Term());
//        initialNote.getTerm().setBasicForm("foobar");
//        concept.getTypes().add(NoteType.HISTORICAL);
//        noteDao.save(initialNote);
//
//        documentNote = documentNoteDao.getById(documentNote.getId());
//        documentNote.setDescription(new Paragraph().toString());
//        documentNote.setDescription(new Paragraph().addElement(new StringElement("foo")).toString());
//        documentNote.getNote().setFormat(NoteFormat.PERSON);
//        documentNote.getNote().setLemmaMeaning("totally different");
//        documentNote.getNote().getPerson().getNormalizedForm().setFirst("something else");
//        documentNote.getNote().getPlace().getNormalizedForm().setName("barfo");
//        documentNote.setSources(new Paragraph().toString());
//        documentNote.setSources(new Paragraph().addElement(new StringElement("bar")).toString());
//        documentNote.setSubtextSources(new Paragraph().toString());
//        documentNote.setSubtextSources(new Paragraph().addElement(new StringElement("foooo")).toString());
//        documentNote.getNote().getTerm().setBasicForm("baaaaar");
//        documentNote.getTypes().add(NoteType.WORD_EXPLANATION);
//        documentNote.getComments().add(new NoteComment(documentNote.getConcept(extendedTerm), "jeejee", "vesa"));
//        Note note = documentNoteDao.getById(documentNote.getId()).getNote();
//        documentNoteDao.saveAsCopy(documentNote);
//
//        DocumentNote copyOfDocumentNote = documentNoteDao.getById(documentNote.getId());
//        Note copyOfNote = copyOfDocumentNote.getNote();
//        assertThat(copyOfNote.getId(), not(note.getId()));
//        assertThat(copyOfNote, not(note));
//        assertThat(copyOfNote.getDescription(), not(note.getDescription()));
//        assertThat(copyOfNote.getFormat(), not(note.getFormat()));
//        assertThat(copyOfNote.getLemmaMeaning(), not(note.getLemmaMeaning()));
//        assertEquals(copyOfNote.getPerson(), note.getPerson());
//        assertEquals(copyOfNote.getPlace(), note.getPlace());
//        assertThat(copyOfNote.getSources(), not(note.getSources()));
//        assertThat(copyOfNote.getSubtextSources(), not(note.getSubtextSources()));
//        assertEquals(copyOfNote.getTerm(), note.getTerm());
//        assertThat(copyOfNote.getTypes(), not(note.getTypes()));
//        assertThat(copyOfNote.getComments(), not(note.getComments()));
//    }

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
        List<DocumentNote> documentNotesOfTerm = documentNoteDao.getOfTerm(term.getId());
        assertEquals(documentNote, documentNotesOfTerm.get(0));
    }

    @Test
    public void Add_The_Same_Word_Twice() {
        String text = "l\u00E4htee";
        Note note = noteDao.createDocumentNote(createNote(), document, text).getNote();
        noteDao.createDocumentNote(note, document, text);
        assertEquals(6, countDocumentNotes(noteDao.listNotes(searchInfo)));
    }

    @Test
    @Ignore
    public void Query_For_Document_Notes_And_Retrieve_The_One_Attached_To_Current_Document() {
        // FIXME
        String text = "l\u00E4htee";
        Note note = noteDao.createDocumentNote(createNote(), document, text).getNote();
        DocumentNote documentNote = new DocumentNote();
        documentNote.setNote(note);
        documentNoteDao.save(documentNote);

        searchInfo.setCurrentDocument(document);
        List<Note> notes = noteDao.listNotes(searchInfo);
        for (Note n : notes){
            for (DocumentNote current : documentNoteDao.getOfNote(n.getId())) {
                if (current.getNote().getLemma().equals(text)) {
                    if (current.getDocument() != null) {
                        System.err.println(current.getDocument().getId());
                    } else {
                        System.err.println("null");
                    }
                }
            }
        }

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
    public void Get_Document_Notes_Of_Note_In_Document() {
        DocumentNote documentNote = documentNoteDao.getOfDocument(document).iterator().next();
        List<DocumentNote> documentNotes = documentNoteDao.getOfNoteInDocument(documentNote
                .getNote().getId(), documentNote.getDocument().getId());
        assertEquals(1, documentNotes.size());
    }

    @Test
    public void Get_Publishable_Notes_Of_Document() throws Exception {
        //TODO
//        String element = "play-act-sp2-p";
//        String text = "sun ullakosta ottaa";
//        DocumentNote documentNote = documentDao.addNote(createNote(), document, new SelectedText(element, element, text));
//        document = documentNote.getDocRevision();
//        documentNote.setPublishable(true);
//        assertTrue(documentNoteDao.getPublishableNotesOfDocument(document).isEmpty());
//        documentNoteDao.save(documentNote);
//        assertEquals(5, documentNoteDao.getOfDocument(document).size());
//        assertEquals(1, documentNoteDao.getPublishableNotesOfDçocument(document).size());
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
