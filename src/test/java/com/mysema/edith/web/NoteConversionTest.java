package com.mysema.edith.web;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;

public class NoteConversionTest {
    
    public static class DocNoteTO {
        
        private Long id;
        
        private Object note, document;
        
        private String path, fullSelection;

        public Object getDocument() {
            return document;
        }

        public String getFullSelection() {
            return fullSelection;
        }

        public Long getId() {
            return id;
        }

        public Object getNote() {
            return note;
        }

        public String getPath() {
            return path;
        }

        public void setDocument(Object document) {
            this.document = document;
        }

        public void setFullSelection(String fullSelection) {
            this.fullSelection = fullSelection;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setNote(Object note) {
            this.note = note;
        }

        public void setPath(String path) {
            this.path = path;
        }
                
    }
    
    private Converter converter = new Converter();
    
    private DocumentNote docNote;
    
    @Before
    public void setUp() {
        Document doc = new Document();
        doc.setId(2l);
        doc.setPath("abc");
        
        Note note = new Note();
        note.setId(3l);
        note.setDescription("description");
        
        docNote = new DocumentNote();
        docNote.setFullSelection("full");
        docNote.setNote(note);
        docNote.setDocument(doc);
    }
    
    @Test
    public void To_DocNoteTO() {               
        DocNoteTO docNoteInfo = converter.convert(docNote, new DocNoteTO());
        assertEquals("full", docNoteInfo.getFullSelection());
        assertEquals(docNote.getDocument(), docNoteInfo.getDocument());
        assertEquals(docNote.getNote(), docNoteInfo.getNote());
    }
    
    @Test
    public void To_DocumentNote() {
        DocNoteTO docNoteInfo = converter.convert(docNote, new DocNoteTO());
        
        DocumentNote docNote2 = converter.convert(docNoteInfo, DocumentNote.class);
        assertEquals(docNote.getDocument().getId(), docNote2.getDocument().getId());
        assertEquals(docNote.getNote().getId(), docNote2.getNote().getId());
    }
    

}
