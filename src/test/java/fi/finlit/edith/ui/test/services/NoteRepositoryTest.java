/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.Test;

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
    private NoteRevisionRepository noteRevisionRepo;
    
    @Inject @Symbol(ServiceTestModule.NOTE_TEST_DATA_KEY)
    private File noteTestData;
    
    @Test
    public void importNotes() throws Exception{
        adminService.removeNotesAndTerms();
        
        assertEquals(133, noteRepo.importNotes(noteTestData));        
        assertEquals(1l, noteRevisionRepo.queryNotes("lemma").getAvailableRows());
        assertEquals(2l, noteRevisionRepo.queryNotes("etten anna sinulle").getAvailableRows());
    }
}
