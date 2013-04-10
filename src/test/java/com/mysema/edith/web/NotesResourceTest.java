package com.mysema.edith.web;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mysema.edith.EdithTestConstants;
import com.mysema.edith.dto.NoteTO;
import com.mysema.edith.services.NoteDao;

public class NotesResourceTest extends AbstractResourceTest {

    @Inject
    private NoteDao noteDao;
    
    @Inject @Named(EdithTestConstants.NOTE_TEST_DATA_KEY)
    private File noteTestData;
    
    @Inject
    private NotesResource notes;
    
    @Before
    public void setUp() {
        noteDao.importNotes(noteTestData);
    }
    
    @Test
    public void GetById() {       
        assertNotNull(notes.getById(1l));
    }
    
    @Test
    public void Add() {
        NoteTO note = new NoteTO();
        notes.create(note);
    }
    
}
