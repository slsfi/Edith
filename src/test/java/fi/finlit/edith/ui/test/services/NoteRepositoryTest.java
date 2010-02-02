/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevisionRepository;
import fi.finlit.edith.ui.services.AdminService;

/**
 * NoteRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRepositoryTest extends AbstractServiceTest{
    
    @Inject
    private AdminService adminService;

    @Inject
    private NoteRepository noteRepo;
    
    @Inject
    private DocumentRepository documentRepo;
    
    @Inject
    private NoteRevisionRepository noteRevisionRepo;
    
    @Inject @Symbol(ServiceTestModule.NOTE_TEST_DATA_KEY)
    private File noteTestData;
    
    @Inject @Symbol(ServiceTestModule.TEST_DOCUMENT_KEY)
    private String testDocument;
    
    @Before
    public void setUp(){
        adminService.removeNotesAndTerms();    
    }
    
    @Test
    public void importNotes() throws Exception{          
        assertEquals(133, noteRepo.importNotes(noteTestData));
        assertEquals(133, noteRevisionRepo.queryNotes("*").getAvailableRows());                
        assertEquals(1, noteRevisionRepo.queryNotes("lemma").getAvailableRows());
        assertEquals(2, noteRevisionRepo.queryNotes("etten anna sinulle").getAvailableRows());
    }
    
    @Test
    public void queryDictionary() throws Exception{
        assertEquals(133, noteRepo.importNotes(noteTestData));        
        assertTrue(noteRepo.queryDictionary("*").getAvailableRows() > 0);
    }
    
    @Test
    @Ignore
    public void createNote() throws SVNException{
        Document document = documentRepo.getDocumentForPath(testDocument);
        List<Long> revisions = documentRepo.getRevisions(document);
        long latestRevision = revisions.get(revisions.size() - 1).longValue();
        
        String lemma = UUID.randomUUID().toString();
        noteRepo.createNote(document.revision(latestRevision), "10", lemma, "longText");
        assertTrue(noteRepo.queryDictionary(lemma).getAvailableRows() > 0);
        
//        assertNotNull(noteRevisionRepo.getByLocalId(document, latestRevision, "10"));
    }

    @Override
    protected Class<?> getServiceClass() {
        return NoteRepository.class;
    }
}
