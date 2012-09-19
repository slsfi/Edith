package com.mysema.edith.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mysema.edith.domain.Document;
import com.mysema.edith.dto.DocumentInfo;
import com.mysema.edith.dto.DocumentNoteInfo;

public class ConverterTest {
    
    @Test
    public void Document() {
        Document doc = new Document();
        doc.setId(3l);
        doc.setPath("abc");
        doc.setTitle("title");
        
        DocumentInfo info = Converter.convert(doc, new DocumentInfo());
        assertEquals(Long.valueOf(3l), info.getId());
        assertEquals("abc", info.getPath());
        assertEquals("title", info.getTitle());
    }
    
    @Test
    public void DocumentNoteInfo() {
        DocumentNoteInfo docNoteInfo = new DocumentNoteInfo();
        
    }
    
    @Test
    public void NoteInfo() {
        
    }
    
    @Test
    public void PersonInfo() {
        
    }
    
    @Test
    public void PlaceInfo() {
        
    }
    
    @Test
    public void UserInfo() {
        
    }

}
