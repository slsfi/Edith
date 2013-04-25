package com.mysema.edith.web;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mysema.edith.EdithTestConstants;
import com.mysema.edith.dto.NoteTO;
import com.mysema.edith.dto.TermTO;
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
    public void Create() {
        NoteTO note = new NoteTO();
        NoteTO created = notes.create(note);
        
        assertNotNull(created.getId());
    }

    @Test
    public void Create_Note_And_Verify_Cascading_Works() {
        TermTO term = new TermTO();
        term.setBasicForm("talo");
        NoteTO note = new NoteTO();
        note.setLemma("talossa");
        note.setTerm(term);
        NoteTO persistedNote = notes.create(note);
        assertNotNull(persistedNote.getId());
        assertNotNull(persistedNote.getTerm().getId());
    }

    @Test
    public void Update_Note() {
        NoteTO note = new NoteTO();
        note.setLemma("talossa");
        note = notes.create(note);

        TermTO term = new TermTO();
        term.setBasicForm("talo");
        note.setTerm(term);
        note = notes.update(note.getId(), note);

        assertNotNull(note.getId());
        assertNotNull(note.getTerm().getId());
    }

}
