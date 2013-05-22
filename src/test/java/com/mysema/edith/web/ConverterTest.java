package com.mysema.edith.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.NameForm;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.NoteFormat;
import com.mysema.edith.domain.NoteStatus;
import com.mysema.edith.domain.NoteType;
import com.mysema.edith.domain.Person;
import com.mysema.edith.domain.Place;
import com.mysema.edith.domain.Profile;
import com.mysema.edith.domain.User;
import com.mysema.edith.dto.DocumentNoteTO;
import com.mysema.edith.dto.DocumentTO;
import com.mysema.edith.dto.NoteTO;
import com.mysema.edith.dto.PersonTO;
import com.mysema.edith.dto.PlaceTO;
import com.mysema.edith.dto.UserTO;

public class ConverterTest {

    private final Converter converter = new Converter();

    @Test
    public void Document() {
        Document doc = new Document();
        doc.setId(3l);
        doc.setPath("abc");
        doc.setTitle("title");

        DocumentTO info = converter.convert(doc, new DocumentTO());
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

        DocumentNoteTO docNoteInfo = converter.convert(docNote, new DocumentNoteTO());
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

        NoteTO noteInfo = converter.convert(note, new NoteTO());
        assertEquals(Long.valueOf(3), noteInfo.getId());
        assertEquals("description", noteInfo.getDescription());
        assertEquals(NoteFormat.NOTE, noteInfo.getFormat());

        PersonTO personInfo = noteInfo.getPerson();
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

        PersonTO personInfo = converter.convert(person, new PersonTO());
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

        PlaceTO placeInfo = converter.convert(place, new PlaceTO());
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

        UserTO info = converter.convert(user, new UserTO());
        assertEquals("x", info.getUsername());
    }

    @Test
    public void From_Map() {
        Map<String, String> contents = Maps.newHashMap();
        contents.put("username", "x");
        contents.put("email", "a@b.com");
        contents.put("firstName", "a");
        contents.put("lastName", "b");
        contents.put("profile", "User");

        User user = converter.convert(contents, new User());
        assertEquals("x", user.getUsername());
        assertEquals("a@b.com", user.getEmail());
        assertEquals("a", user.getFirstName());
        assertEquals("b", user.getLastName());
        assertEquals(Profile.User, user.getProfile());
    }

    @Test
    public void From_Map_Which_Contains_A_List_Of_Enums() {
        Map<String, Object> contents = Maps.newHashMap();
        List<String> types = Lists.newArrayList();
        types.add("WORD_EXPLANATION");
        contents.put("types", types);
        Note note = converter.convert(contents, new Note());
        assertTrue(note.getTypes().contains(NoteType.WORD_EXPLANATION));
    }

}
