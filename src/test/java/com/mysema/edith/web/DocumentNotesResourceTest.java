package com.mysema.edith.web;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mysema.edith.EdithTestConstants;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.Term;
import com.mysema.edith.domain.User;
import com.mysema.edith.dto.DocumentNoteTO;
import com.mysema.edith.services.DocumentDao;
import com.mysema.edith.services.NoteDao;
import com.mysema.edith.services.UserDao;

@Ignore
public class DocumentNotesResourceTest extends AbstractResourceTest {
    
    @Inject @Named(EdithTestConstants.TEST_DOCUMENT_KEY)
    private String testDocument;
    
    @Inject
    private DocumentNotesResource documentNotes;
    
    @Inject
    private DocumentDao documentDao;
    
    @Inject 
    private NoteDao noteDao;
    
    @Inject
    private UserDao userDao;
    
    private Note note;
    
    private Note createNote(User editor) {
        Note note = new Note();
        note.setTerm(new Term());
        note.setAllEditors(Sets.newHashSet(editor));
        note.setLastEditedBy(editor);
        return note;
    }
    
    @Before
    public void setUp() {
        User editor = new User();
        editor.setUsername("test"+System.currentTimeMillis());
        userDao.save(editor);
        
        note  = createNote(editor);
        noteDao.save(note);
    }
    
    @Test
    public void GetById() {
        Document document = documentDao.getDocumentForPath(testDocument);
        DocumentNote documentNote = noteDao.createDocumentNote(note, document, 
                "l\u00E4htee h\u00E4ihins\u00E4 Mikko Vilkastuksen");
        assertNotNull(documentNotes.getById(documentNote.getId()));
    }
    
    @Test
    public void Add() {
        Document document = documentDao.getDocumentForPath(testDocument);
        DocumentNoteTO info = new DocumentNoteTO();
        info.setDocument(document.getId());
        info.setFullSelection("a");
        info.setNote(note.getId());
        documentNotes.create(info);
    }

}
