/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mysema.edith.EdithTestConstants;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.Term;
import com.mysema.edith.domain.User;
import com.mysema.edith.dto.FullDocumentNoteTO;
import com.mysema.edith.dto.NoteSearchTO;
import com.mysema.edith.dto.SelectedText;
import com.mysema.edith.services.DocumentDao;
import com.mysema.edith.services.NoteAdditionFailedException;
import com.mysema.edith.services.NoteDao;
import com.mysema.edith.services.UserDao;

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
    public void Create() {
        Document document = documentDao.getDocumentForPath(testDocument);
        Map<String, Object> info = Maps.newHashMap();
        info.put("document", document.getId());
        info.put("fullSelection", "a");
        info.put("note", note.getId());
        FullDocumentNoteTO created = documentNotes.create(info);

        assertNotNull(created.getId());
    }

    @Test
    public void Create_without_Note() {
        Document document = documentDao.getDocumentForPath(testDocument);
        Map<String, Object> info = Maps.newHashMap();
        info.put("document", document.getId());
        info.put("fullSelection", "a");
        FullDocumentNoteTO created = documentNotes.create(info);

        assertNotNull(created.getId());
    }

    @Test
    @Ignore
    public void Create_Selection() throws IOException, NoteAdditionFailedException {
        String PREFIX = "TEI-text0-body0-";
        Document document = documentDao.getDocumentForPath(testDocument);
        String element = "div0-div1-sp1-p0";
        String text = "sun ullakosta ottaa";

        Map<String, Object> info = Maps.newHashMap();
        info.put("document", document.getId());
        info.put("note", note.getId());
        info.put("selection", new SelectedText(PREFIX + element, PREFIX + element, 1, 4, text));

        FullDocumentNoteTO created = documentNotes.create(info);

        assertNotNull(created.getId());
    }

    @Test
    public void Search() {
        Map<String, Object> rv = documentNotes.all(null, null, null, null, null);
        assertNotNull(rv);

        assertEquals(1l, rv.get("currentPage"));
        assertEquals(25l, rv.get("perPage"));
    }

    @Test
    public void Query() {
        NoteSearchTO search = new NoteSearchTO();
        Map<String, Object> result = documentNotes.query(search);
        assertNotNull(result);

        assertEquals(1l, result.get("currentPage"));
        assertEquals(25l, result.get("perPage"));
    }

    @Test
    public void Query_Order() {
        NoteSearchTO search = new NoteSearchTO();
        search.setOrder("fullSelection");
        Map<String, Object> result = documentNotes.query(search);
        assertNotNull(result);
    }
}
