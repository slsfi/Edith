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
import org.junit.Ignore;
import org.junit.Test;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteComment;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;
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
    private NoteRevisionRepository noteRevisionRepo;

    @Inject
    @Symbol(ServiceTestModule.NOTE_TEST_DATA_KEY)
    private File noteTestData;

    @Inject
    @Symbol(ServiceTestModule.TEST_DOCUMENT_KEY)
    private String testDocument;

    @Before
    public void setUp() {
        adminService.removeNotesAndTerms();
    }

    @Test
    public void importNote() throws Exception {
        noteRepo.importNotes(noteTestData);
        GridDataSource gridDataSource = noteRevisionRepo.queryNotes("kereitten");
        gridDataSource.prepare(0, 10000, new ArrayList<SortConstraint>());
        NoteRevision note = (NoteRevision) gridDataSource.getRowValue(0);
        assertNotNull(note);
        assertEquals("kereitten", note.getLemma());
        assertEquals("'keritte'", note.getLemmaMeaning());
        assertEquals("(murt. kerii ’keriä’, ks. <bibliograph>Itkonen 1989</bibliograph> , 363).",
                note.getDescription().toString().replaceAll("\\s+", " ").trim());
        assertEquals("<bibliograph>v</bibliograph>", note.getSources().toString().replaceAll("\\s+", " ").trim());
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
    @Ignore
    public void createNote() {
        Document document = documentRepo.getDocumentForPath(testDocument);
        List<RevisionInfo> revisions = documentRepo.getRevisions(document);
        long latestRevision = revisions.get(revisions.size() - 1).getSvnRevision();

        String longText = UUID.randomUUID().toString();
        noteRepo.createNote(document.getRevision(latestRevision), "10", longText);
        assertTrue(noteRepo.queryDictionary(longText).getAvailableRows() > 0);

        // assertNotNull(noteRevisionRepo.getByLocalId(document, latestRevision, "10"));
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
    public void getLemmaForLongText() {
        assertEquals("word", getLemmaForLongText("word"));
        assertEquals("word1 word2", getLemmaForLongText("word1 word2"));
        assertEquals("word1 \u2013 \u2013 word3", getLemmaForLongText("word1 word2 word3"));
        assertEquals("word1 \u2013 \u2013 word3", getLemmaForLongText("word1\t word2 \nword3"));
        assertEquals("foo \u2013 \u2013 bar",
                getLemmaForLongText(" \n      foo \n \t baz    bar    \n\t\t"));
        assertEquals("foo", getLemmaForLongText(" \n      foo \n \t   \n\t\t"));
    }

    private String getLemmaForLongText(String longText) {
        NoteRevision rev = new NoteRevision();
        rev.setLongText(longText);
        rev.setLemmaFromLongText();
        return rev.getLemma();
    }

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

    @Override
    protected Class<?> getServiceClass() {
        return NoteRepository.class;
    }
}
