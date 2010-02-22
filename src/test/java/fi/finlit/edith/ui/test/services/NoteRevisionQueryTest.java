/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Collections;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevisionRepository;
import fi.finlit.edith.ui.services.AdminService;

/**
 * NoteRevisionQueryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRevisionQueryTest extends AbstractServiceTest{
    
    @Inject
    private NoteRepository noteRepo;

    @Inject
    private AdminService adminService;

    @Inject
    private NoteRevisionRepository noteRevisionRepo;
    
    @Inject @Symbol(ServiceTestModule.NOTE_TEST_DATA_KEY)
    private File noteTestData;
    
    @Before
    public void setUp() throws Exception {
        adminService.removeNotesAndTerms();
        assertEquals(133, noteRepo.importNotes(noteTestData));
    }
    
    @After
    public void tearDown(){
        adminService.removeNotesAndTerms();
    }
    
    @Test
    public void queryNotes(){
        noteRevisionRepo.queryNotes("*").getAvailableRows();
    }
    
    @Test
    public void queryNotes_kaikki() throws Exception{        
        GridDataSource dataSource = noteRevisionRepo.queryNotes("kaikki");
        assertEquals(3, dataSource.getAvailableRows());
        dataSource.prepare(0, 2, Collections.<SortConstraint>emptyList());
        for (int i = 0; i < 3; i++){
            assertNotNull("Value at index " + i + " was null", dataSource.getRowValue(i));
        }
    }
    
    @Override
    protected Class<?> getServiceClass() {
        return null;
    }   

}
