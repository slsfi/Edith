/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;
import fi.finlit.edith.ui.services.AdminService;
import fi.finlit.edith.ui.services.svn.RevisionInfo;

/**
 * NoteRevisionRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRevisionRepositoryTest extends AbstractServiceTest{

    @Inject @Symbol(ServiceTestModule.TEST_DOCUMENT_KEY)
    private String testDocument;

    @Inject
    private NoteRepository noteRepo;

    @Inject
    private AdminService adminService;

    @Inject
    private NoteRevisionRepository noteRevisionRepo;

    @Inject
    private DocumentRepository documentRepo;
    
    private Document document;
    
    private DocumentRevision docRev;

    private long latestRevision;

    @Before
    public void setUp() {
        adminService.removeNotesAndTerms();

        document = documentRepo.getDocumentForPath(testDocument);
        List<RevisionInfo> revisions = documentRepo.getRevisions(document);
        latestRevision = revisions.get(revisions.size() - 1).getSvnRevision();

        docRev = document.getRevision(latestRevision);
        noteRepo.createNote(docRev, "1", "l\u00E4htee h\u00E4ihins\u00E4 Mikko Vilkastuksen");
        noteRepo.createNote(docRev, "2", "koska suutarille k\u00E4skyn k\u00E4r\u00E4jiin annoit, saadaksesi naimalupaa.");
        noteRepo.createNote(docRev, "3", "tulee, niin seisoo s\u00E4\u00E4t\u00F6s-kirjassa.");
        noteRepo.createNote(docRev, "4", "kummallenkin m\u00E4\u00E4r\u00E4tty, niin emmep\u00E4 tiet\u00E4isi t\u00E4ss\u00E4");
    }

    @Test
    public void getByLocalId() throws SVNException{
        assertNotNull(noteRevisionRepo.getByLocalId(docRev, "1"));
        assertNotNull(noteRevisionRepo.getByLocalId(docRev, "2"));
        assertNotNull(noteRevisionRepo.getByLocalId(docRev, "3"));
        assertNotNull(noteRevisionRepo.getByLocalId(docRev, "4"));
    }

    @Test
    public void queryNotes(){
        assertTrue(noteRevisionRepo.queryNotes("annoit").getAvailableRows() > 0);
    }

    @Test
    public void getOfDocument(){
        assertEquals(4, noteRevisionRepo.getOfDocument(docRev).size());
    }

    @Test
    public void getOfDocument_with_note_updates() throws InterruptedException{
        assertEquals(4, noteRevisionRepo.getOfDocument(docRev).size());

        for (NoteRevision rev : noteRevisionRepo.getOfDocument(docRev)){
            rev = rev.createCopy();
            rev.setLemma(rev.getLemma() +"XXX");
            noteRevisionRepo.save(rev);
        }

        assertEquals(4, noteRevisionRepo.getOfDocument(docRev).size());
    }

    @Override
    protected Class<?> getServiceClass() {
        return NoteRevisionRepository.class;
    }
}
