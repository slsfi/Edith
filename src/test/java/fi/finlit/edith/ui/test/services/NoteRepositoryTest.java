/*
 * Copyright (c) 2009 Mysema Ltd.
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.Before;
import org.junit.Test;

import com.mysema.rdfbean.tapestry.BeanGridDataSource;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.EdithTestConstants;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteSearchInfo;
import fi.finlit.edith.domain.NameForm;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteComment;
import fi.finlit.edith.domain.Person;
import fi.finlit.edith.domain.Place;
import fi.finlit.edith.domain.Term;
import fi.finlit.edith.domain.TermLanguage;
import fi.finlit.edith.domain.User;
import fi.finlit.edith.domain.UserInfo;
import fi.finlit.edith.ui.services.AdminService;
import fi.finlit.edith.ui.services.DocumentNoteRepository;
import fi.finlit.edith.ui.services.DocumentRepository;
import fi.finlit.edith.ui.services.NoteRepository;
import fi.finlit.edith.ui.services.NoteWithInstances;
import fi.finlit.edith.ui.services.PersonRepository;
import fi.finlit.edith.ui.services.PlaceRepository;
import fi.finlit.edith.ui.services.UserRepository;
import fi.finlit.edith.ui.services.svn.RevisionInfo;

public class NoteRepositoryTest extends AbstractServiceTest {

    @Inject
    private AdminService adminService;

    @Inject
    private NoteRepository noteRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private DocumentRepository documentRepository;

    @Inject
    private DocumentNoteRepository documentNoteRepository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private PlaceRepository placeRepository;

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
        adminService.removeNotesAndTerms();
    }

    private int countDocumentNotes(List<NoteWithInstances> notes){
        int count = 0;
        for (NoteWithInstances n : notes){
            count += n.getDocumentNotes().size();
        }
        return count;
    }

    @Test
    public void Note_Id_Equals_Concept_Id(){
        Note note = createNote();
        noteRepository.save(note);
        if (!extendedTerm) {
            assertEquals(note.getId(), note.getConcept(extendedTerm).getId());
        }
    }

    @Test
    public void CreateComment_And_Load() {
        Note note = createNote();
        noteRepository.save(note);
        NoteComment comment = noteRepository.createComment(note.getConcept(extendedTerm), "boomboomboom");
        NoteComment loaded = noteRepository.getCommentById(comment.getId());
        assertEquals(comment.getId(), loaded.getId());
        assertEquals(note.getConcept(extendedTerm).getId(), loaded.getConcept().getId());
        assertEquals(comment.getConcept().getId(), loaded.getConcept().getId());
    }

    @Test
    public void CreateComment() {
        Note note = createNote();
        noteRepository.save(note);
        NoteComment comment = noteRepository.createComment(note.getConcept(extendedTerm), "boomboomboom");
        Collection<NoteComment> comments = noteRepository.getById(note.getId()).getConcept(extendedTerm).getComments();
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
    public void CreateNote() {
        Document document = documentRepository.getOrCreateDocumentForPath(testDocument);

        String longText = "two words";
        DocumentNote documentNote = noteRepository.createDocumentNote(createNote(), document.getRevision(-1),
                longText);

        assertNotNull(documentNote);
    }

    @Test
    public void LoadById() {
        Note note = createNote();
        noteRepository.save(note);

        Note loaded = noteRepository.getById(note.getId());
        assertNotNull(loaded);
        assertNotNull(loaded.getConcept(extendedTerm));

    }

    @Test
    public void CreateNote_Note_With_The_Lemma_Already_Exists_Notes_Are_Same() {
        Document document = documentRepository.getOrCreateDocumentForPath(testDocument);

        String longText = "two words";
        DocumentNote documentNote = noteRepository.createDocumentNote(createNote(), document.getRevision(-1),
                longText);
        DocumentNote documentNote2 = noteRepository.createDocumentNote(documentNote.getNote(), document.getRevision(-1),
                longText);
        assertEquals(documentNote.getNote().getId(), documentNote2.getNote().getId());
    }

    @Test
    public void Find() {
        Document document = documentRepository.getOrCreateDocumentForPath(testDocument);
        noteRepository.createDocumentNote(createNote(), document.getRevision(-1), "foobar");
        Note note = noteRepository.find("foobar");
        assertNotNull(note);
    }

    @Test
    public void ImportNote() throws Exception {
        noteRepository.importNotes(noteTestData);
        Note note = noteRepository.find("kereitten");
        assertNotNull(note);
        assertEquals("kereitten", note.getLemma());
        assertEquals("'keritte'", note.getLemmaMeaning());
        String description = note.getConcept(extendedTerm).getDescription();
        String sources = note.getConcept(extendedTerm).getSources();
        assertEquals(
                "(murt. kerii ’keri\u00E4’, ks. <bibliograph>Itkonen 1989</bibliograph> , 363).",
                description.replaceAll("\\s+", " ").trim());
        assertEquals("<bibliograph>v</bibliograph>",
                sources.replaceAll("\\s+", " ").trim());
    }

    @Test
    public void Add_Note_With_Existing_Orphan() {
        noteRepository.importNotes(noteTestData);
        String lemma = "riksi\u00E4";
        Note note = noteRepository.find(lemma);
        assertNotNull(note);
        Document document = documentRepository.getOrCreateDocumentForPath(testDocument);
        DocumentNote documentNote = noteRepository.createDocumentNote(note, document.getRevision(-1), "123456",
                lemma, 0);
        assertNotNull(documentNote);
        assertEquals(note.getId(), documentNote.getNote().getId());
        assertNotNull(documentNoteRepository.getById(documentNote.getId())
                .getNote());
        assertEquals(1, documentNoteRepository.queryNotes(lemma).getAvailableRows());
    }

    @Test
    public void Add_Note_With_Existing_Orphan_Verify_Sources_And_Description_Correct() {
        noteRepository.importNotes(noteTestData);
        String lemma = "riksi\u00E4";
        Note note = noteRepository.find(lemma);
        assertNotNull(note);
        Document document = documentRepository.getOrCreateDocumentForPath(testDocument);
        DocumentNote documentNote = noteRepository.createDocumentNote(note, document.getRevision(-1), "123456",
                lemma, 0);
        assertNotNull(documentNote);
        assertEquals(note.getId(), documentNote.getNote().getId());
        assertEquals(note.getConcept(extendedTerm).getDescription(),
                documentNoteRepository.getById(documentNote.getId()).getConcept(extendedTerm).getDescription());
        assertEquals(note.getConcept(extendedTerm).getSources(),
                documentNoteRepository.getById(documentNote.getId()).getConcept(extendedTerm).getSources());
    }

    @Test
    public void Import_The_Same_Notes_Twice() {
        assertEquals(9, noteRepository.importNotes(noteTestData));
        assertEquals(9, noteRepository.importNotes(noteTestData));
        assertEquals(18, noteRepository.getAll().size());
    }

    @Test
    public void importNotes() throws Exception {
        assertEquals(9, noteRepository.importNotes(noteTestData));
        assertEquals(9, noteRepository.getAll().size());
        assertNotNull(noteRepository.find("kereitten"));
    }

    @Test
    public void QueryDictionary() throws Exception {
        assertEquals(9, noteRepository.importNotes(noteTestData));
        assertEquals(extendedTerm ? 9 : 0, noteRepository.queryDictionary("*").getAvailableRows());

    }

    @Test
    public void QueryDictionary2() throws Exception {
        assertEquals(9, noteRepository.importNotes(noteTestData));
        GridDataSource dataSource = noteRepository.queryDictionary("a");
        int count1 = dataSource.getAvailableRows();
        int count2 = dataSource.getAvailableRows();
        assertEquals(count1, count2);
    }

    @Test
    public void Remove() {
        Document document = documentRepository.getOrCreateDocumentForPath(testDocument);
        List<RevisionInfo> revisions = documentRepository.getRevisions(document);
        long latestRevision = revisions.get(revisions.size() - 1).getSvnRevision();

        String longText = UUID.randomUUID().toString();
        noteRepository.createDocumentNote(createNote(), document.getRevision(latestRevision), longText);
    }

    @Test
    public void RemoveComment() {
        Note note = createNote();
        noteRepository.save(note);
        NoteComment comment = noteRepository.createComment(note.getConcept(extendedTerm), "boomboomboom");
        Collection<NoteComment> comments = noteRepository.getById(note.getId()).getConcept(extendedTerm).getComments();
        assertEquals(1, comments.size());
        assertEquals(comment.getMessage(), comments.iterator().next().getMessage());
        noteRepository.removeComment(comment.getId());
        comments = noteRepository.getById(note.getId()).getConcept(extendedTerm).getComments();
        assertTrue(comments.isEmpty());
    }

    @Test
    public void Find_Notes() {
        noteRepository.importNotes(noteTestData);
        assertEquals(1, noteRepository.findNotes("kereitten").size());
    }

    @Test
    public void Find_All_Notes_With_Search() {
        Document document = documentRepository.getOrCreateDocumentForPath(testDocument);
        String longText = "two words";
        noteRepository.createDocumentNote(createNote(), document.getRevision(-1), longText);
        DocumentNoteSearchInfo search = new DocumentNoteSearchInfo();
        //Empty finds all
        assertEquals(1, noteRepository.findAllNotes(search).size());

        //With document we should find our note
        search.setCurrentDocument(document);
        assertEquals(1, noteRepository.findAllNotes(search).size());

        //False hit
        userRepository.save(new User("dummy"));
        search.setCreators(Collections.singleton(new UserInfo("dummy")));
        assertEquals(0, noteRepository.findAllNotes(search).size());

    }

    @Test
    public void Find_Notes_With_Paged_Search() {
        Note note1 = createNote("note1");
        Note note2 = createNote("note2");
        Note note3 = createNote("note3");

        DocumentNoteSearchInfo search = new DocumentNoteSearchInfo();
        search.setFullText("foo");
        assertRowCount(0, search);
 
        search.setFullText("note");
        assertRowCount(3, search);

        search.setOrphans(true);
        assertRowCount(3, search);

        Document document = documentRepository.getOrCreateDocumentForPath(testDocument);
        search.setDocuments(Collections.singleton(document));
        search.setOrphans(false);
        assertRowCount(0, search);
        

        //Adding note to document
        noteRepository.createDocumentNote(note1, document.getRevision(-1), "a");
        search.setOrphans(false);
        assertRowCount(1, search);
        assertRowValues(search, note1);

        //Create different document
        Document otherDoc = documentRepository.getOrCreateDocumentForPath("/documents/nummi.xml");
        noteRepository.createDocumentNote(note2, otherDoc.getRevision(-1), "b");
        
        //notes 1 and 2 should be found
        search.addDocument(otherDoc);
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
    
    private void assertRowCount(int expected, DocumentNoteSearchInfo search) {
        assertEquals(expected, noteRepository.findNotes(search).getAvailableRows());
    }
    
    private void assertRowValues(DocumentNoteSearchInfo search, Note ... notes ) {
        GridDataSource src = noteRepository.findNotes(search);
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
        noteRepository.save(note1);
        Note note2 = createNote("note2");
        note2.getTerm().setLanguage(TermLanguage.SWEDISH);
        noteRepository.save(note2);
        
        DocumentNoteSearchInfo search = new DocumentNoteSearchInfo();
        assertEquals(2, noteRepository.findNotes(search).getAvailableRows());
        
        search.setLanguage(TermLanguage.FINNISH);
        assertEquals(1, noteRepository.findNotes(search).getAvailableRows());
        
        
        
    }
    
    @Test
    public void Find_Notes_With_Paged_Search_By_Fulltext() {
        Note note1 = createNote("a");
        
        assertFullText("foo",0);
        assertFullText("a", 1);
        assertFullText("b", 0);
        
        note1.getTerm().setBasicForm("b");
        noteRepository.save(note1);
        
        assertFullText("b", 1);
        assertFullText("c", 0);
        
        note1.getTerm().setMeaning("c");
        noteRepository.save(note1);
        assertFullText("c", 1);
        assertFullText("d", 0);
        
        note1.getConcept(extendedTerm).setDescription("d");
        noteRepository.save(note1);
        
        Note note2 = noteRepository.getById(note1.getId());
        assertEquals("d", note2.getConcept(extendedTerm).getDescription());
        
        assertFullText("d", 1);
        assertFullText("e", 0);
        
        note1.getConcept(extendedTerm).setSources("e");
        noteRepository.save(note1);
        assertFullText("e", 1);
        assertFullText("f", 0);
        
        noteRepository.createComment(note1.getConcept(extendedTerm), "f");
        assertFullText("f", 1);
        
    }
    
    private void assertFullText(String fulltext, int expected) {
        DocumentNoteSearchInfo search = new DocumentNoteSearchInfo();
        search.setFullText(fulltext);
        assertEquals(expected, noteRepository.findNotes(search).getAvailableRows());
    }
    
    

//    If we are using remote permanently, then this test is not necessary
//    @Test
//    public void Find_Notes_As_Orpan_With_Deleted_Doc_Notes() {
//        Document document = documentRepository.getOrCreateDocumentForPath(testDocument);
//        String longText = "two words";
//        DocumentNote docNote = noteRepository.createDocumentNote(createNote(), document.getRevision(-1), "10", longText);
//
//        DocumentNoteSearchInfo search = new DocumentNoteSearchInfo(document);
//        assertEquals(1, noteRepository.findNotes(search).size());
//
//    }
    
    @Test
    public void Increment_Note_DocumentNote_Count() {
        Note note = createNote("a");
        assertEquals(0, noteRepository.getById(note.getId()).getDocumentNoteCount());
        note.incDocumentNoteCount();
        noteRepository.save(note);
        assertEquals(1, noteRepository.getById(note.getId()).getDocumentNoteCount());
        
    }
    
    @Test
    public void Remove_Based_On_Revision() {
        Document document = documentRepository.getOrCreateDocumentForPath(testDocument);
        String longText = "two words";
        Note note = createNote();
        DocumentNote documentNote = noteRepository.createDocumentNote(note, document.getRevision(-1), longText);
        assertEquals(1, noteRepository.getById(note.getId()).getDocumentNoteCount());
        List<NoteWithInstances> notes = noteRepository.findNotesWithInstances(new DocumentNoteSearchInfo(document));
        assertTrue(countDocumentNotes(notes) > 0);
        noteRepository.remove(documentNote, documentNote.getSVNRevision());
        assertEquals(0, noteRepository.getById(note.getId()).getDocumentNoteCount());
        
        notes = noteRepository.findNotesWithInstances(new DocumentNoteSearchInfo(document));
        assertEquals(0, countDocumentNotes(notes));
        
    }

    @Test
    public void Query_All_Persons() throws Exception {
        assertEquals(0, noteRepository.queryPersons("*").getAvailableRows());
        personRepository.save(new Person());
        assertEquals(1, noteRepository.queryPersons("*").getAvailableRows());
    }

    @Test
    public void Query_Persons() throws Exception {
        assertEquals(0, noteRepository.queryPersons("Aapel").getAvailableRows());
        personRepository.save(new Person(new NameForm("Aapeli", "Aapelsson", null), null));
        assertEquals(0, noteRepository.queryPersons("Aabel").getAvailableRows());
        assertEquals(1, noteRepository.queryPersons("Aapel").getAvailableRows());
    }

    @Test
    public void Query_All_Places() throws Exception {
        assertEquals(0, noteRepository.queryPlaces("*").getAvailableRows());
        placeRepository.save(new Place());
        assertEquals(1, noteRepository.queryPlaces("*").getAvailableRows());
    }

    @Test
    public void Query_Places_Based_On_Name() throws Exception {
        assertEquals(0, noteRepository.queryPlaces("Helsin").getAvailableRows());
        placeRepository.save(new Place(new NameForm("Helsinki", null), null));
        assertEquals(0, noteRepository.queryPlaces("Helssin").getAvailableRows());
        assertEquals(1, noteRepository.queryPlaces("Helsin").getAvailableRows());
    }

    private Note createNote() {
        Note note = new Note();
        note.setTerm(new Term());
        return note;
    }

    private Note createNote(String lemma) {
       Note note = createNote();
       note.setLemma(lemma);
       noteRepository.save(note);
       return note;
    }

}
