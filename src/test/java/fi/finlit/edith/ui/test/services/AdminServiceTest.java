/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.After;
import org.junit.Test;

import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.Term;
import fi.finlit.edith.domain.TermLanguage;
import fi.finlit.edith.domain.TermRepository;
import fi.finlit.edith.ui.services.AdminService;

/**
 * AdminServiceTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class AdminServiceTest extends AbstractServiceTest{

    @Inject
    private NoteRepository noteRepository;

    @Inject
    private TermRepository termRepository;

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
        noteRepository.importNotes(noteTestData);
        assertFalse(noteRepository.getAll().isEmpty());

        adminService.removeNotes();
        assertTrue(noteRepository.getAll().isEmpty());
    }

    @Test
    public void removeNotesAndTerms() throws Exception {
        noteRepository.importNotes(noteTestData);
        Term term = new Term();
        term.setBasicForm("mutta");
        term.setMeaning("joku konjunktio");
        term.setLanguage(TermLanguage.FINNISH);
        termRepository.save(term);
        assertFalse(noteRepository.getAll().isEmpty());
        assertFalse(termRepository.getAll().isEmpty());

        adminService.removeNotesAndTerms();
        assertTrue(noteRepository.getAll().isEmpty());
        assertTrue(termRepository.getAll().isEmpty());
    }
}
