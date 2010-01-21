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
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;
import fi.finlit.edith.ui.services.AdminService;

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
    
    private long latestRevision;
    
    @Before
    public void setUp() throws SVNException{
        adminService.removeNotesAndTerms();
        
        document = documentRepo.getDocumentForPath(testDocument);
        List<Long> revisions = documentRepo.getRevisions(document);
        latestRevision = revisions.get(revisions.size() - 1).longValue();       
        
        noteRepo.createNote(document, latestRevision, "1", "lähtee häihinsä", "lähtee häihinsä Mikko Vilkastuksen");
        noteRepo.createNote(document, latestRevision, "2", "käskyn annoit", "koska suutarille käskyn käräjiin annoit, saadaksesi naimalupaa.");
        noteRepo.createNote(document, latestRevision, "3", "tulee", "tulee, niin seisoo säätös-kirjassa.");
        noteRepo.createNote(document, latestRevision, "4", "määrätty", "kummallenkin määrätty, niin emmepä tietäisi tässä");
        
    }
    
    @Test
    public void getByLocalId() throws SVNException{        
        assertNotNull(noteRevisionRepo.getByLocalId(document, latestRevision, "1"));
        assertNotNull(noteRevisionRepo.getByLocalId(document, latestRevision, "2"));
        assertNotNull(noteRevisionRepo.getByLocalId(document, latestRevision, "3"));
        assertNotNull(noteRevisionRepo.getByLocalId(document, latestRevision, "4"));
    }

    @Test
    public void queryNotes(){
        assertTrue(noteRevisionRepo.queryNotes("annoit").getAvailableRows() > 0);
    }
    
    @Test
    public void getOfDocument(){
        assertEquals(4, noteRevisionRepo.getOfDocument(document, latestRevision).size());
    }
    
    @Test
    public void getOfDocument_with_note_updates() throws InterruptedException{
        assertEquals(4, noteRevisionRepo.getOfDocument(document, latestRevision).size());
        
        Thread.sleep(2000);
        
        for (NoteRevision rev : noteRevisionRepo.getOfDocument(document, latestRevision)){
            rev = rev.createCopy();
            rev.setLemma(rev.getLemma() +"XXX");
            noteRevisionRepo.save(rev);
        }
        
//        for (NoteRevision rev : noteRevisionRepo.getOfDocument(document, latestRevision)){
//            System.out.println(rev.getCreatedOn());
//        }    
        
        assertEquals(4, noteRevisionRepo.getOfDocument(document, latestRevision).size());
    }

    @Override
    protected Class<?> getServiceClass() {
        return NoteRevisionRepository.class;
    }
}
