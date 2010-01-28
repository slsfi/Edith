/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.After;
import org.junit.Test;

import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevisionRepository;
import fi.finlit.edith.ui.services.AdminService;

/**
 * AdminServiceTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class AdminServiceTest extends AbstractServiceTest{
    
    @Inject
    private NoteRepository noteRepo;
    
    @Inject
    private NoteRevisionRepository noteRevisionRepo;
    
    @Inject
    private AdminService adminService;

    @Inject @Symbol(ServiceTestModule.NOTE_TEST_DATA_KEY)
    private File noteTestData;
    
    @After
    public void tearDown(){
        System.out.println();
    }
    
    @Test
    public void removeNotes() throws Exception {
        long s = System.currentTimeMillis();
        noteRepo.importNotes(noteTestData);
        long e = System.currentTimeMillis();
        System.err.println("Import took " + (e-s) + "ms");
        assertTrue(noteRevisionRepo.queryNotes("*").getAvailableRows() > 0);
        
        s = System.currentTimeMillis();
        adminService.removeNotes();
        e = System.currentTimeMillis();
        System.err.println("Removal took " + (e-s) + "ms");
        assertTrue(noteRevisionRepo.queryNotes("*").getAvailableRows() == 0);
    }

    @Test
    public void removeNotesAndTerms() throws Exception {
        long s = System.currentTimeMillis();
        noteRepo.importNotes(noteTestData);
        long e = System.currentTimeMillis();
        System.err.println("Import took " + (e-s) + "ms");
        assertTrue(noteRevisionRepo.queryNotes("*").getAvailableRows() > 0);
        
        s = System.currentTimeMillis();
        adminService.removeNotesAndTerms();
        e = System.currentTimeMillis();
        System.err.println("Removal took " + (e-s) + "ms");
        assertTrue(noteRevisionRepo.queryNotes("*").getAvailableRows() == 0);
    }

    @Override
    protected Class<?> getServiceClass() {
        return AdminService.class;
    }

}
