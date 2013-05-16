/*
 * Copyright (c) 2009 Mysema Ltd.
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
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.Transactional;
import com.mysema.edith.EDITH;
import com.mysema.edith.EdithTestConstants;
import com.mysema.edith.domain.*;
import com.mysema.edith.dto.DocumentTO;
import com.mysema.edith.dto.NoteSearchTO;
import com.mysema.edith.dto.UserTO;
import com.mysema.query.SearchResults;

@Transactional
public class NoteDaoTest extends AbstractHibernateTest {

    private static final String KEYTERM = "term.basicForm";
    private static final String DATE = "editedOn";
    private static final String USER = "lastEditedBy.username";
    private static final String STATUS = "status";
    private static final String LEMMA = "lemma";
    
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

    @Inject @Named(EdithTestConstants.NOTE_TEST_DATA_KEY)
    private File noteTestData;

    @Inject @Named(EdithTestConstants.TEST_DOCUMENT_KEY)
    private String testDocument;

    @Inject @Named(EDITH.EXTENDED_TERM)
    private boolean extendedTerm;
    
    @Before
    public void setUp() {
        if (userDao.getAll().isEmpty()) {
            userDao.save(new User("timo"));
        }
    }

    @Test
    public void Save_As_New() {
        Note note = new Note();
        note.setLemma("foobar");
        noteDao.save(note);
        Long id = note.getId();

        noteDao.saveAsNew(note);
        Long otherId = note.getId();

        assertNotNull(otherId);
        assertFalse(id.equals(otherId));
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
    public void ImportNotes() throws Exception {
        assertEquals(9, noteDao.importNotes(noteTestData));
        assertEquals(9, query().from(qnote).count());
        assertNotNull(findByLemma("kereitten"));
    }

//    @Test
//    public void QueryDictionary() throws Exception {
//        assertEquals(9, noteDao.importNotes(noteTestData));
//        assertEquals(extendedTerm ? 9 : 0, noteDao.queryDictionary("*").getTotal());
//    }

//    @Test
//    public void QueryDictionary2() throws Exception {
//        assertEquals(9, noteDao.importNotes(noteTestData));
//        GridDataSource dataSource = noteDao.queryDictionary("a");
//        int count1 = dataSource.getTotal();
//        int count2 = dataSource.getTotal();
//        assertEquals(count1, count2);
//    }

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

    private DocumentTO convert(Document doc) {
        DocumentTO to = new DocumentTO();
        to.setId(doc.getId());
        to.setPath(doc.getPath());
        return to;
    }
    
    @Test
    public void Find_Notes_With_Paged_Search() {
        Note note1 = createNote("note1");
        Note note2 = createNote("note2");
        Note note3 = createNote("note3");

        NoteSearchTO search = new NoteSearchTO();
        search.setFullText("foo");
        assertRowCount(0, search);

        search.setFullText("note");
        assertRowCount(3, search);

        search.setOrphans(true);
        assertRowCount(3, search);

        Document document = documentDao.getDocumentForPath(testDocument);
        search.getDocuments().add(convert(document));
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
        search.getDocuments().add(convert(otherDoc));
        assertRowCount(2, search);
        assertRowValues(search, note1, note2);

        //with orphans the result is the only orphan
        search.setOrphans(true);
        assertRowCount(1, search);
        assertRowValues(search, note3);

        //with all, we get all + orphans
        search.setDocuments(Collections.<DocumentTO>emptySet());
        search.setIncludeAllDocs(true);
        assertRowCount(3, search);

        //with all - orphans
        search.setOrphans(false);
        assertRowCount(2, search);
    }

    @Test
    public void Find_With_Lemma_No_Term() {        
        String lemma = "XXX" + System.currentTimeMillis() + "XXX";
        Note note = new Note();
        note.setLemma(lemma);
        noteDao.save(note);
        
        NoteSearchTO search = new NoteSearchTO();
        search.setFullText(lemma);
        SearchResults<Note> ds = noteDao.findNotes(search);
        assertEquals(1l, ds.getTotal());
    }
    
    @Test
    public void Find_With_Lemma_Of_Given_Docs() {
        String lemma = "XXX" + System.currentTimeMillis() + "XXX";
        Note note = createNote(lemma);
        
        Document document = documentDao.getDocumentForPath(testDocument);
        
        NoteSearchTO search = new NoteSearchTO();
        search.setDocuments(Collections.singleton(convert(document)));
        search.setFullText(lemma);
        SearchResults<Note> ds = noteDao.findNotes(search);
        assertEquals(0, ds.getTotal());
        
        noteDao.createDocumentNote(note, document, "a");
        assertEquals(1, noteDao.findNotes(search).getTotal());
    }
    
    @Test
    public void Find_With_Lemma_Of_All_Docs() {
        String lemma = "XXX" + System.currentTimeMillis() + "XXX";
        Note note = createNote(lemma);
        
        Document document = documentDao.getDocumentForPath(testDocument);
        
        NoteSearchTO search = new NoteSearchTO();
        search.setIncludeAllDocs(true);
        search.setFullText(lemma);
        SearchResults<Note> ds = noteDao.findNotes(search);
        assertEquals(0, ds.getTotal());
        
        noteDao.createDocumentNote(note, document, "a");
        assertEquals(1, noteDao.findNotes(search).getTotal());
    }
        
    @Test
    public void Find_With_Creators() {
        createNote("note1");
        createNote("note2");
        createNote("note3");

        User user = userDao.getCurrentUser();
        NoteSearchTO search = new NoteSearchTO();
        search.getCreators().add(new UserTO(user.getId(), user.getUsername()));
        noteDao.findNotes(search);
        // TODO
    }

    @Test
    public void Find_With_Formats() {
        createNote("note1");
        createNote("note2");
        createNote("note3");

        NoteSearchTO search = new NoteSearchTO();
        search.getNoteFormats().add(NoteFormat.NOTE);
        noteDao.findNotes(search);
        // TODO
    }

    @Test
    public void Find_With_Types() {
        createNote("note1");
        createNote("note2");
        createNote("note3");

        NoteSearchTO search = new NoteSearchTO();
        search.getNoteTypes().add(NoteType.CRITIQUE);
        noteDao.findNotes(search);
        // TODO
    }

    @Test
    public void Find_With_Order_Date() {
        noteDao.importNotes(noteTestData);
        NoteSearchTO search = new NoteSearchTO();
        search.setOrderBy(DATE);
        SearchResults<Note> data = noteDao.findNotes(search);
        assertTrue(data.getTotal() > 0);
    }

    @Test
    public void Find_With_Order_Keyterm() {
        noteDao.importNotes(noteTestData);
        NoteSearchTO search = new NoteSearchTO();
        search.setOrderBy(KEYTERM);
        SearchResults<Note> data = noteDao.findNotes(search);
        assertTrue(data.getTotal() > 0);                
    }
    
    @Test
    public void Find_With_Order_Lemma() {
        noteDao.importNotes(noteTestData);
        NoteSearchTO search = new NoteSearchTO();
        search.setOrderBy(LEMMA);
        SearchResults<Note> data = noteDao.findNotes(search);
        assertTrue(data.getTotal() > 0);
    }

    @Test
    public void Find_With_Order_Status() {
        noteDao.importNotes(noteTestData);
        NoteSearchTO search = new NoteSearchTO();
        search.setOrderBy(STATUS);
        SearchResults<Note> data = noteDao.findNotes(search);
        assertTrue(data.getTotal() > 0);
    }

    @Test
    public void Find_With_Order_User() {
        noteDao.importNotes(noteTestData);
        NoteSearchTO search = new NoteSearchTO();
        search.setOrderBy(STATUS);
        noteDao.findNotes(search);
        // TODO
    }
    
    @Test
    public void Find_With_Path() {
        Note note = createNote("lemma");  
        Document document = documentDao.getDocumentForPath(testDocument);
        
        noteDao.createDocumentNote(note, document, "a");
        NoteSearchTO search = new NoteSearchTO();
        search.setPaths(Collections.singleton("/nothere"));
        
        assertEquals(0, noteDao.findNotes(search).getTotal());
        
        search.setPaths(Collections.singleton("/documents"));
        
        assertEquals(1, noteDao.findNotes(search).getTotal());
    }
    
    private void assertRowCount(int expected, NoteSearchTO search) {
        assertEquals(expected, noteDao.findNotes(search).getTotal());
    }

    private void assertRowValues(NoteSearchTO search, Note ... notes ) {
        SearchResults<Note> src = noteDao.findNotes(search);
        assertTrue(src.getTotal() >= notes.length );
        
        int i = 0;
        for(Note note : notes) {
            Note val = (Note)src.getResults().get(i++);
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

        NoteSearchTO search = new NoteSearchTO();
        assertEquals(2, noteDao.findNotes(search).getTotal());

        search.setLanguage(TermLanguage.FINNISH);
        assertEquals(1, noteDao.findNotes(search).getTotal());
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
        NoteSearchTO search = new NoteSearchTO();
        search.setFullText(fulltext);
        assertEquals(expected, noteDao.findNotes(search).getTotal());
    }

//    @Test
//    public void Query_All_Persons() throws Exception {
//        assertEquals(0, noteDao.queryPersons("*").getTotal());
//        personDao.save(new Person());
//        assertEquals(1, noteDao.queryPersons("*").getTotal());
//    }
//
//    @Test
//    public void Query_Persons() throws Exception {
//        assertEquals(0, noteDao.queryPersons("Aapel").getTotal());
//        personDao.save(new Person(new NameForm("Aapeli", "Aapelsson", null), null));
//        assertEquals(0, noteDao.queryPersons("Aabel").getTotal());
//        assertEquals(1, noteDao.queryPersons("Aapel").getTotal());
//    }
//
//    @Test
//    public void Query_All_Places() throws Exception {
//        assertEquals(0, noteDao.queryPlaces("*").getTotal());
//        placeDao.save(new Place());
//        assertEquals(1, noteDao.queryPlaces("*").getTotal());
//    }
//
//    @Test
//    public void Query_Places_Based_On_Name() throws Exception {
//        assertEquals(0, noteDao.queryPlaces("Helsin").getTotal());
//        NameForm normalizedForm = new NameForm("Helsinki", null);
//        placeDao.save(new Place(normalizedForm, null));
//        assertEquals(0, noteDao.queryPlaces("Helssin").getTotal());
//        assertEquals(1, noteDao.queryPlaces("Helsin").getTotal());
//    }

//    @Test
//    public void Query_Notes() {
//        noteDao.importNotes(noteTestData);
//        GridDataSource dataSource = noteDao.queryNotes("XXX");
//        assertEquals(0, dataSource.getTotal());
//    }
//
//    @Test
//    public void Query_Notes_Wildcard() {
//        noteDao.importNotes(noteTestData);
//        GridDataSource dataSource = noteDao.queryNotes("*");
//        assertTrue(dataSource.getTotal() > 0);
//    }

    @Test
    public void Get_Orphan_Ids() {
        noteDao.importNotes(noteTestData);
        assertFalse(noteDao.getOrphanIds().isEmpty());
    }

//    @Test
//    public void Remove_Note() {
//        noteDao.importNotes(noteTestData);
//        GridDataSource data = noteDao.queryNotes("*");
//        assertTrue(data.getTotal() > 0);
//        data.prepare(0, data.getTotal(), Collections.<SortConstraint>emptyList());
//
//        int max = data.getTotal();
//        for (int i = 0; i < max; i++) {
//            noteDao.remove((Note)data.getRowValue(i));
//        }
//        assertEquals(0, data.getTotal());
//    }

//    @Test
//    public void Remove_Notes() {
//        noteDao.importNotes(noteTestData);
//        GridDataSource data = noteDao.queryNotes("*");
//        assertTrue(data.getTotal() > 0);
//        data.prepare(0, data.getTotal(), Collections.<SortConstraint>emptyList());
//
//        int max = data.getTotal();
//        List<Note> notes = new ArrayList<Note>(max);
//        for (int i = 0; i < max; i++) {
//            notes.add((Note)data.getRowValue(i));
//        }
//        noteDao.removeNotes(notes);
//        assertEquals(0, data.getTotal());
//    }

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
