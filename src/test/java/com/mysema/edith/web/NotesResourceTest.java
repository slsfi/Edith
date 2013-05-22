package com.mysema.edith.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mysema.edith.EdithTestConstants;
import com.mysema.edith.dto.NoteSearchTO;
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
        
        Map<String, Object> contents = Maps.newHashMap();
        contents.put("lemma", note.getLemma());
        contents.put("term", term);
        contents.put("id", note.getId());
        note = notes.update(note.getId(), contents);

        assertNotNull(note.getId());
        assertNotNull(note.getTerm().getId());
    }
    
    @Test
    public void Search() {
        Map<String, Object> rv = notes.all(null, null, null, null, null);
        assertNotNull(rv);
    }
    
    @Test
    public void Query() {
        NoteSearchTO search = new NoteSearchTO();
        Map<String, Object> result = notes.query(search);
        assertNotNull(result);
    }
    
    @Test
    public void Query_Csv() {
        NoteSearchTO search = new NoteSearchTO();
        search.setColumns(Arrays.asList("lemma", "description", "subtextSources", "editedOn", 
                "term.basicForm", "term.meaning")); 
        Response result = notes.queryCsv(search);
        String csv = result.getEntity().toString();
        assertTrue(csv.startsWith("lemma;description;subtextSources;editedOn;term.basicForm;term.meaning"));        
    }
    
    @Test
    public void Query_Csv_No_Columns() {
        NoteSearchTO search = new NoteSearchTO();
        Response result = notes.queryCsv(search);
        String csv = result.getEntity().toString();
        assertEquals("", csv);
    }
    
    @Test
    public void Import_Notes() {
        // TODO
    }

}
