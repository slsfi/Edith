/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteRepository;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteComment;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.ui.services.AdminService;
import fi.finlit.edith.ui.services.svn.RevisionInfo;

/**
 * NoteRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRepositoryTest extends AbstractServiceTest {

    @Inject
    private AdminService adminService;

    @Inject
    private NoteRepository noteRepo;

    @Inject
    private DocumentRepository documentRepo;

    @Inject
    private DocumentNoteRepository noteRevisionRepo;

    @Inject
    @Symbol(ServiceTestModule.NOTE_TEST_DATA_KEY)
    private File noteTestData;

    @Inject
    @Symbol(ServiceTestModule.TEST_DOCUMENT_KEY)
    private String testDocument;

    @Test
    public void createComment() {
        Note note = new Note();
        noteRepo.save(note);
        NoteComment comment = noteRepo.createComment(note, "boomboomboom");
        Collection<NoteComment> comments = noteRepo.getById(note.getId()).getComments();
        assertEquals(1, comments.size());
        assertEquals(comment.getMessage(), comments.iterator().next().getMessage());
    }

    @Test
    public void createLemmaForLongText() {
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
    public void createNote() {
        Document document = documentRepo.getDocumentForPath(testDocument);

        String longText = "two words";
        DocumentNote documentNote = noteRepo.createNote(document.getRevision(-1), "10", longText);

        assertNotNull(documentNote);
    }

    @Test
    public void createNote_Note_With_The_Lemma_Already_Exists_Notes_Are_Same() {
        Document document = documentRepo.getDocumentForPath(testDocument);

        String longText = "two words";
        DocumentNote documentNote = noteRepo.createNote(document.getRevision(-1), "10", longText);
        DocumentNote documentNote2 = noteRepo.createNote(document.getRevision(-1), "11", longText);
        assertEquals(documentNote.getNote().getId(), documentNote2.getNote().getId());
    }

    @Test
    public void find() {
        Document document = documentRepo.getDocumentForPath(testDocument);
        noteRepo.createNote(document.getRevision(-1), "lid1234", "foobar");
        Note note = noteRepo.find("foobar");
        assertNotNull(note);
    }

    @Override
    protected Class<?> getServiceClass() {
        return NoteRepository.class;
    }

    @Test
    public void importNote() throws Exception {
        noteRepo.importNotes(noteTestData);
        GridDataSource gridDataSource = noteRevisionRepo.queryNotes("kereitten");
        gridDataSource.prepare(0, 10000, new ArrayList<SortConstraint>());
        DocumentNote note = (DocumentNote) gridDataSource.getRowValue(0);
        assertNotNull(note);
        assertEquals("kereitten", note.getNote().getLemma());
        assertEquals("'keritte'", note.getNote().getLemmaMeaning());
        assertEquals("(murt. kerii ’keriä’, ks. <bibliograph>Itkonen 1989</bibliograph> , 363).",
                note.getNote().getDescription().toString().replaceAll("\\s+", " ").trim());
        assertEquals("<bibliograph>v</bibliograph>", note.getNote().getSources().toString()
                .replaceAll("\\s+", " ").trim());
    }

    @Test
    public void importNotes() throws Exception {
        assertEquals(9, noteRepo.importNotes(noteTestData));
        assertEquals(9, noteRevisionRepo.queryNotes("*").getAvailableRows());
        assertEquals(1, noteRevisionRepo.queryNotes("kereitten").getAvailableRows());
    }

    @Test
    public void queryDictionary() throws Exception {
        assertEquals(9, noteRepo.importNotes(noteTestData));
        assertEquals(0, noteRepo.queryDictionary("*").getAvailableRows());
    }

    @Test
    public void queryDictionary2() throws Exception {
        assertEquals(9, noteRepo.importNotes(noteTestData));
        GridDataSource dataSource = noteRepo.queryDictionary("a");
        int count1 = dataSource.getAvailableRows();
        int count2 = dataSource.getAvailableRows();
        assertEquals(count1, count2);
    }

    @Test
    public void remove() {
        Document document = documentRepo.getDocumentForPath(testDocument);
        List<RevisionInfo> revisions = documentRepo.getRevisions(document);
        long latestRevision = revisions.get(revisions.size() - 1).getSvnRevision();

        String longText = UUID.randomUUID().toString();
        noteRepo.createNote(document.getRevision(latestRevision), "10", longText);
    }

    @Test
    public void removeComment() {
        Note note = new Note();
        noteRepo.save(note);
        NoteComment comment = noteRepo.createComment(note, "boomboomboom");
        Collection<NoteComment> comments = noteRepo.getById(note.getId()).getComments();
        assertEquals(1, comments.size());
        assertEquals(comment.getMessage(), comments.iterator().next().getMessage());
        noteRepo.removeComment(comment.getId());
        comments = noteRepo.getById(note.getId()).getComments();
        assertTrue(comments.isEmpty());
    }

    @Before
    public void setUp() {
        adminService.removeNotesAndTerms();
    }
}
