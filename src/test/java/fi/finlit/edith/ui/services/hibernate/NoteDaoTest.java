/*
 * Copyright (c) 2009 Mysema Ltd.
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.EdithTestConstants;
import fi.finlit.edith.dto.NoteSearchInfo;
import fi.finlit.edith.dto.UserInfo;
import fi.finlit.edith.sql.domain.*;
import fi.finlit.edith.ui.services.DocumentDao;
import fi.finlit.edith.ui.services.DocumentNoteDao;
import fi.finlit.edith.ui.services.NoteDao;
import fi.finlit.edith.ui.services.PersonDao;
import fi.finlit.edith.ui.services.PlaceDao;
import fi.finlit.edith.ui.services.UserDao;

public class NoteDaoTest extends AbstractHibernateTest {

    @Inject
    private NoteDao noteDao;

    @Inject
    private DocumentDao documentDao;

    @Inject
    private DocumentNoteDao documentNoteDao;

    @Inject
    private PersonDao personDao;

    @Inject
    private PlaceDao placeDao;

    @Inject
    private UserDao userDao;

    @Inject
    @Symbol(EdithTestConstants.NOTE_TEST_DATA_KEY)
    private File noteTestData;

    @Inject
    @Symbol(EdithTestConstants.TEST_DOCUMENT_KEY)
    private String testDocument;

    @Inject
    @Symbol(EDITH.EXTENDED_TERM)
    private boolean extendedTerm;

    @Before
    public void setUp() {
        if (userDao.getAll().isEmpty()) {
            userDao.save(new User("timo"));
        }
    }

    @Test
    public void CreateComment() {
        Note note = createNote();
        noteDao.save(note);
        NoteComment comment = noteDao.createComment(note, "boomboomboom");
        Collection<NoteComment> comments = noteDao.getById(note.getId()).getComments();
        assertEquals(1, comments.size());
        assertEquals(comment.getMessage(), comments.iterator().next().getMessage());
    }

    @Test
    public void CreateLemmaForLongText() {
        assertEquals("word", Note.createLemmaFromLongText("word"));
        assertEquals("word1 word2", Note.createLemmaFromLongText("word1 word2"));
        assertEquals("word1 \u2013 \u2013 word3", Note.createLemmaFromLongText("word1 word2 word3"));
        assertEquals("word1 \u2013 \u2013 word3",
                Note.createLemmaFromLongText("word1\t word2 \nword3"));
        assertEquals("foo \u2013 \u2013 bar",
                Note.createLemmaFromLongText(" \n      foo \n \t baz    bar    \n\t\t"));
        assertEquals("foo", Note.createLemmaFromLongText(" \n      foo \n \t   \n\t\t"));
    }

    @Test
    public void Create_Note() {
        Document document = documentDao.getDocumentForPath(testDocument);

        String longText = "two words";
        DocumentNote documentNote = noteDao.createDocumentNote(createNote(), document,
                longText);

        assertNotNull(documentNote);
    }

    @Test
    public void LoadById() {
        Note note = createNote();
        noteDao.save(note);

        Note loaded = noteDao.getById(note.getId());
        assertNotNull(loaded);
        assertNotNull(loaded);

    }

    @Test
    public void CreateNote_Note_With_The_Lemma_Already_Exists_Notes_Are_Same() {
        Document document = documentDao.getDocumentForPath(testDocument);

        String longText = "two words";
        DocumentNote documentNote = noteDao.createDocumentNote(createNote(), document,
                longText);
        DocumentNote documentNote2 = noteDao.createDocumentNote(documentNote.getNote(), document,
                longText);
        assertEquals(documentNote.getNote().getId(), documentNote2.getNote().getId());
    }

    private static final QNote qnote = QNote.note;

    @Test
    public void ImportNote() throws Exception {
        noteDao.importNotes(noteTestData);
        Note note = findByLemma("kereitten");
        assertNotNull(note);
        assertEquals("kereitten", note.getLemma());
        assertEquals("'keritte'", note.getLemmaMeaning());
        String description = note.getDescription();
        String sources = note.getSources();
        assertEquals(
                "(murt. kerii ’keri\u00E4’, ks. <bibliograph>Itkonen 1989</bibliograph> , 363).",
                description.replaceAll("\\s+", " ").trim());
        assertEquals("<bibliograph>v</bibliograph>",
                sources.replaceAll("\\s+", " ").trim());
    }

    private Note findByLemma(String string) {
        return query().from(qnote).where(qnote.lemma.eq(string)).singleResult(qnote);
    }

    @Test
    public void Add_Note_With_Existing_Orphan() {
        noteDao.importNotes(noteTestData);
        String lemma = "riksi\u00E4";
        Note note = findByLemma(lemma);
        assertNotNull(note);
        Document document = documentDao.getDocumentForPath(testDocument);
        DocumentNote documentNote = noteDao.createDocumentNote(note, document,
                lemma);
        assertNotNull(documentNote);
        assertEquals(note.getId(), documentNote.getNote().getId());
        assertNotNull(documentNoteDao.getById(documentNote.getId())
                .getNote());
    }

    @Test
    public void Add_Note_With_Existing_Orphan_Verify_Sources_And_Description_Correct() {
        noteDao.importNotes(noteTestData);
        String lemma = "riksi\u00E4";
        Note note = findByLemma(lemma);
        assertNotNull(note);
        Document document = documentDao.getDocumentForPath(testDocument);
        DocumentNote documentNote = noteDao.createDocumentNote(note, document,
                lemma);
        assertNotNull(documentNote);
        assertEquals(note.getId(), documentNote.getNote().getId());
        assertEquals(note.getDescription(),
                documentNoteDao.getById(documentNote.getId()).getNote().getDescription());
        assertEquals(note.getSources(),
                documentNoteDao.getById(documentNote.getId()).getNote().getSources());
    }

    @Test
    public void Import_The_Same_Notes_Twice() {
        assertEquals(9, noteDao.importNotes(noteTestData));
        assertEquals(9, noteDao.importNotes(noteTestData));
        assertEquals(18, query().from(qnote).count());
    }

    @Test
    public void importNotes() throws Exception {
        assertEquals(9, noteDao.importNotes(noteTestData));
        assertEquals(9, query().from(qnote).count());
        assertNotNull(findByLemma("kereitten"));
    }

    @Test
    public void QueryDictionary() throws Exception {
        assertEquals(9, noteDao.importNotes(noteTestData));
        assertEquals(extendedTerm ? 9 : 0, noteDao.queryDictionary("*").getAvailableRows());

    }

    @Test
    public void QueryDictionary2() throws Exception {
        assertEquals(9, noteDao.importNotes(noteTestData));
        GridDataSource dataSource = noteDao.queryDictionary("a");
        int count1 = dataSource.getAvailableRows();
        int count2 = dataSource.getAvailableRows();
        assertEquals(count1, count2);
    }

    @Test
    public void RemoveComment() {
        Note note = createNote();
        noteDao.save(note);
        NoteComment comment = noteDao.createComment(note, "boomboomboom");
        Collection<NoteComment> comments = noteDao.getById(note.getId()).getComments();
        assertEquals(1, comments.size());
        assertEquals(comment.getMessage(), comments.iterator().next().getMessage());
        noteDao.removeComment(comment.getId());
        comments = noteDao.getById(note.getId()).getComments();
        assertTrue(comments.isEmpty());
    }

    @Test
    public void Find_Notes_With_Paged_Search() {
        Note note1 = createNote("note1");
        Note note2 = createNote("note2");
        Note note3 = createNote("note3");

        NoteSearchInfo search = new NoteSearchInfo();
        search.setFullText("foo");
        assertRowCount(0, search);

        search.setFullText("note");
        assertRowCount(3, search);

        search.setOrphans(true);
        assertRowCount(3, search);

        Document document = documentDao.getDocumentForPath(testDocument);
        search.getDocuments().add(document);
        search.setOrphans(false);
        assertRowCount(0, search);


        //Adding note to document
        noteDao.createDocumentNote(note1, document, "a");
        search.setOrphans(false);
        assertRowCount(1, search);
        assertRowValues(search, note1);

        //Create different document
        Document otherDoc = documentDao.getDocumentForPath("/documents/nummi.xml");
        noteDao.createDocumentNote(note2, otherDoc, "b");

        //notes 1 and 2 should be found
        search.getDocuments().add(otherDoc);
        assertRowCount(2, search);
        assertRowValues(search, note1, note2);

        //with orphans the result is the only orphan
        search.setOrphans(true);
        assertRowCount(1, search);
        assertRowValues(search, note3);

        //with all, we get all + orphans
        search.setDocuments(Collections.<Document>emptySet());
        search.setIncludeAllDocs(true);
        assertRowCount(3, search);

        //with all - orphans
        search.setOrphans(false);
        assertRowCount(2, search);
    }

    @Test
    public void Find_With_Creators() {
        createNote("note1");
        createNote("note2");
        createNote("note3");
        
        User user = userDao.getCurrentUser();
        NoteSearchInfo search = new NoteSearchInfo();
        search.getCreators().add(new UserInfo(user.getId(), user.getUsername()));
        noteDao.findNotes(search);
        // TODO
    }
    
    @Test
    public void Find_With_Formats() {
        createNote("note1");
        createNote("note2");
        createNote("note3");
        
        NoteSearchInfo search = new NoteSearchInfo();
        search.getNoteFormats().add(NoteFormat.NOTE);
        noteDao.findNotes(search);
        // TODO
    }
    
    @Test
    public void Find_With_Types() {
        createNote("note1");
        createNote("note2");
        createNote("note3");
        
        NoteSearchInfo search = new NoteSearchInfo();
        search.getNoteTypes().add(NoteType.CRITIQUE);
        noteDao.findNotes(search);
        // TODO
    }
    
    private void assertRowCount(int expected, NoteSearchInfo search) {
        assertEquals(expected, noteDao.findNotes(search).getAvailableRows());
    }

    private void assertRowValues(NoteSearchInfo search, Note ... notes ) {
        GridDataSource src = noteDao.findNotes(search);
        assertTrue(src.getAvailableRows() >= notes.length );
        src.prepare(0, notes.length+1, Collections.<SortConstraint>emptyList());
        int i = 0;
        for(Note note : notes) {
            Note val = (Note)src.getRowValue(i++);
            assertNotNull(val);
            assertEquals(note.getId(), val.getId());
        }
    }

    @Test
    public void Find_Notes_With_Paged_Search_By_Language() {
        Note note1 = createNote("note1");
        note1.getTerm().setLanguage(TermLanguage.FINNISH);
        noteDao.save(note1);
        Note note2 = createNote("note2");
        note2.getTerm().setLanguage(TermLanguage.SWEDISH);
        noteDao.save(note2);

        NoteSearchInfo search = new NoteSearchInfo();
        assertEquals(2, noteDao.findNotes(search).getAvailableRows());

        search.setLanguage(TermLanguage.FINNISH);
        assertEquals(1, noteDao.findNotes(search).getAvailableRows());
    }

    @Test
    public void Find_Notes_With_Paged_Search_By_Fulltext() {
        Note note1 = createNote("a");

        assertFullText("foo",0);
        assertFullText("a", 1);
        assertFullText("b", 0);

        note1.getTerm().setBasicForm("b");
        noteDao.save(note1);

        assertFullText("b", 1);
        assertFullText("c", 0);

        note1.getTerm().setMeaning("c");
        noteDao.save(note1);
        assertFullText("c", 1);
        assertFullText("d", 0);

        note1.setDescription("d");
        noteDao.save(note1);

        Note note2 = noteDao.getById(note1.getId());
        assertEquals("d", note2.getDescription());

        assertFullText("d", 1);
        assertFullText("e", 0);

        note1.setSources("e");
        noteDao.save(note1);
        assertFullText("e", 1);
        assertFullText("f", 0);

        noteDao.createComment(note1, "f");
        assertFullText("f", 1);

    }

    private void assertFullText(String fulltext, int expected) {
        NoteSearchInfo search = new NoteSearchInfo();
        search.setFullText(fulltext);
        assertEquals(expected, noteDao.findNotes(search).getAvailableRows());
    }

    @Test
    public void Query_All_Persons() throws Exception {
        assertEquals(0, noteDao.queryPersons("*").getAvailableRows());
        personDao.save(new Person());
        assertEquals(1, noteDao.queryPersons("*").getAvailableRows());
    }

    @Test
    public void Query_Persons() throws Exception {
        assertEquals(0, noteDao.queryPersons("Aapel").getAvailableRows());
        personDao.save(new Person(new NameForm("Aapeli", "Aapelsson", null), null));
        assertEquals(0, noteDao.queryPersons("Aabel").getAvailableRows());
        assertEquals(1, noteDao.queryPersons("Aapel").getAvailableRows());
    }

    @Test
    public void Query_All_Places() throws Exception {
        assertEquals(0, noteDao.queryPlaces("*").getAvailableRows());
        placeDao.save(new Place());
        assertEquals(1, noteDao.queryPlaces("*").getAvailableRows());
    }

    @Test
    public void Query_Places_Based_On_Name() throws Exception {
        assertEquals(0, noteDao.queryPlaces("Helsin").getAvailableRows());
        NameForm normalizedForm = new NameForm("Helsinki", null);
        placeDao.save(new Place(normalizedForm, null));
        assertEquals(0, noteDao.queryPlaces("Helssin").getAvailableRows());
        assertEquals(1, noteDao.queryPlaces("Helsin").getAvailableRows());
    }

    private Note createNote() {
        Note note = new Note();
        //XXX Whats best way to deal with this, we always going to have this
        note.setTerm(new Term());
        return note;
    }

    private Note createNote(String lemma) {
       Note note = createNote();
       note.setLemma(lemma);
       noteDao.save(note);
       return note;
    }

}
