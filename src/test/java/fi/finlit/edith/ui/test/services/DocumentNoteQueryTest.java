/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collections;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.ui.services.AdminService;
import fi.finlit.edith.ui.services.DocumentNoteDao;

public class DocumentNoteQueryTest extends AbstractServiceTest{

    @Inject
    private AdminService adminService;

    @Inject
    private DocumentNoteDao documentNoteRepository;

    @Before
    public void setUp() throws Exception {
        adminService.removeNotesAndTerms();
        DocumentNote documentNote = new DocumentNote();
        Note note = new Note();
        note.setLemma("kaikki");
        documentNote.setNote(note);
        documentNoteRepository.save(documentNote);
    }

    @After
    public void tearDown(){
        adminService.removeNotesAndTerms();
    }

    @Test
    public void QueryNotes(){
        assertEquals(1, documentNoteRepository.queryNotes("*").getAvailableRows());
    }

    @Test
    public void QueryNotes_All() throws Exception{
        GridDataSource dataSource = documentNoteRepository.queryNotes("*");
        assertEquals(1, dataSource.getAvailableRows());
        dataSource.prepare(0, 1, Collections.<SortConstraint>emptyList());
        for (int i = 0; i < 1; i++){
            assertNotNull("Value at index " + i + " was null", dataSource.getRowValue(i));
        }
        assertNull(dataSource.getRowValue(1));
    }
}
