package com.mysema.edith.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.NameForm;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.NoteFormat;
import com.mysema.edith.domain.Person;
import com.mysema.edith.domain.Place;
import com.mysema.edith.domain.Profile;
import com.mysema.edith.domain.User;
import com.mysema.edith.dto.DocumentInfo;
import com.mysema.edith.dto.DocumentNoteInfo;
import com.mysema.edith.dto.NoteInfo;
import com.mysema.edith.dto.PersonInfo;
import com.mysema.edith.dto.PlaceInfo;
import com.mysema.edith.dto.UserInfo;

public class ConverterTest {
    
    private Converter converter = new Converter();
    
    @Test
    public void Document() {
        Document doc = new Document();
        doc.setId(3l);
        doc.setPath("abc");
        doc.setTitle("title");
        
        DocumentInfo info = converter.convert(doc, new DocumentInfo());
        assertEquals(Long.valueOf(3l), info.getId());
        assertEquals("abc", info.getPath());
        assertEquals("title", info.getTitle());
    }
    
    @Test
    public void DocumentNoteInfo() {
        Document doc = new Document();
        doc.setId(2l);
        doc.setPath("abc");
        
        Note note = new Note();
        note.setId(3l);
        note.setDescription("description");
        
        DocumentNote docNote = new DocumentNote();
        docNote.setFullSelection("full");
        docNote.setNote(note);
        docNote.setDocument(doc);
        
        DocumentNoteInfo docNoteInfo = converter.convert(docNote, new DocumentNoteInfo());
        assertEquals("full", docNoteInfo.getFullSelection());
        assertEquals(Long.valueOf(2), docNoteInfo.getDocument());
        assertEquals(Long.valueOf(3), docNoteInfo.getNote());
    }
    
    @Test
    public void NoteInfo() {
        Person person = new Person();
        person.setId(4l);
        
        Note note = new Note();
        note.setId(3l);
        note.setDescription("description");
        note.setFormat(NoteFormat.NOTE);
        note.setPerson(person);
        
        NoteInfo noteInfo = converter.convert(note, new NoteInfo());
        assertEquals(Long.valueOf(3), noteInfo.getId());
        assertEquals("description", noteInfo.getDescription());
        assertEquals(NoteFormat.NOTE, noteInfo.getFormat());
        
        PersonInfo personInfo = noteInfo.getPerson();
        assertEquals(Long.valueOf(4l), personInfo.getId());
        
    }
    
    @Test
    public void PersonInfo() {
        NameForm nameForm = new NameForm();
        nameForm.setDescription("abc");
        nameForm.setFirst("a");
        nameForm.setLast("b");
        
        Person person = new Person();
        person.setId(4l);
        person.setNormalized(nameForm);
        person.setOtherForms(Sets.newHashSet(nameForm));
        
        PersonInfo personInfo = converter.convert(person, new PersonInfo());
        assertEquals(Long.valueOf(4l), personInfo.getId());
        
        assertEquals("abc", personInfo.getNormalized().getDescription());
        assertEquals("a", personInfo.getNormalized().getFirst());
        assertEquals("b", personInfo.getNormalized().getLast());
        
        NameForm other = personInfo.getOtherForms().iterator().next();
        assertEquals("abc", other.getDescription());
        assertEquals("a", other.getFirst());
        assertEquals("b", other.getLast());
    }
    
    @Test
    public void PlaceInfo() {
        NameForm nameForm = new NameForm();
        nameForm.setDescription("abc");
        nameForm.setFirst("a");
        nameForm.setLast("b");
        
        Place place = new Place();
        place.setId(4l);
        place.setNormalized(nameForm);
        
        PlaceInfo placeInfo = converter.convert(place, new PlaceInfo());
        assertEquals(Long.valueOf(4), placeInfo.getId());
        
        assertEquals("abc", placeInfo.getNormalized().getDescription());
        assertEquals("a", placeInfo.getNormalized().getFirst());
        assertEquals("b", placeInfo.getNormalized().getLast());
    }
    
    @Test
    public void UserInfo() {
        User user = new User();
        user.setUsername("x");
        user.setEmail("a@b.com");
        user.setFirstName("a");
        user.setLastName("b");
        user.setProfile(Profile.User);
        
        UserInfo info = converter.convert(user, new UserInfo());
        assertEquals("x", info.getUsername());
    }

}
