package com.mysema.edith.web;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mysema.edith.EdithTestConstants;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.Term;
import com.mysema.edith.services.DocumentDao;
import com.mysema.edith.services.NoteDao;

public class DocumentNotesResourceTest extends AbstractResourceTest {
    
    @Inject @Named(EdithTestConstants.TEST_DOCUMENT_KEY)
    private String testDocument;
    
    @Inject
    private DocumentNotesResource documentNotes;
    
    @Inject
    private DocumentDao documentDao;
    
    @Inject
    private NoteDao noteDao;
    
    private Note createNote() {
        Note note = new Note();
        note.setTerm(new Term());
        return note;
    }
    
    @Test
    public void GetById() {
        Document document = documentDao.getDocumentForPath(testDocument);
        DocumentNote documentNote = noteDao.createDocumentNote(createNote(), document, 
                "l\u00E4htee h\u00E4ihins\u00E4 Mikko Vilkastuksen");
        assertNotNull(documentNotes.getById(documentNote.getId()));
    }

}
