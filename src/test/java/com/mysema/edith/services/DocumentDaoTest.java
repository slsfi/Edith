/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.Transactional;
import com.mysema.edith.EDITH;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.NoteComment;
import com.mysema.edith.domain.Term;
import com.mysema.edith.domain.User;
import com.mysema.edith.dto.SelectedText;

public class DocumentDaoTest extends AbstractHibernateTest {
    
    private static final String PREFIX = "TEI-text0-body0-";

    @Inject
    private DocumentDao documentDao;

    @Inject
    private DocumentNoteService documentNoteService;
    
    @Inject
    private NoteDao noteDao;

    @Inject
    private VersioningDao subversionService;

    @Inject
    private UserDao userDao;

    @Inject @Named(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;

    @Inject @Named(EDITH.EXTENDED_TERM)
    private boolean extendedTerm;

    @Before
    public void setUp() throws Exception {
        subversionService.initialize();
        if (userDao.getAll().isEmpty()) {
            userDao.save(new User("timo"));
        }
    }

    @After
    public void tearDown() {
        subversionService.destroy();
    }

    @Test
    public void AddDocuments_From_Zip() {
        File file = new File("src/test/resources/tei.zip");
        assertEquals(5, documentDao.addDocumentsFromZip("/documents/parent", file));

        assertEquals(5, subversionService.getFileItems("/documents/parent", -1).size());
    }

    @Test
    public void AddNote() throws Exception {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "div0-div1-sp1-p0";
        String text = "sun ullakosta ottaa";

        DocumentNote note = documentNoteService.attachNote(createNote(), document, 
                new SelectedText(PREFIX + element, PREFIX + element, 1, 4, text));

        String content = getContent(document.getPath(), -1);
//        System.err.println(content);
        Long localId = note.getId();
        assertTrue(content.contains(start(localId) + text + end(localId)));
    }

    private String getContent(String svnPath, long svnRevision) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = register(subversionService.getStream(svnPath, svnRevision));
        IOUtils.copy(in, out);
        in.close();
        out.close();
        return new String(out.toByteArray(), "UTF-8");
    }

    private Document getDocument(String path) {
        return documentDao.getDocumentForPath(documentRoot + path);
    }

    @Test
    public void GetDocument() {
        String path = "/a/b/name";
        Document document = documentDao.getDocumentForPath(path);
        assertEquals("name", document.getTitle());
        assertEquals(path, document.getPath());
    }

    @Test
    public void GetDocuments_Of_Folder() {
        assertEquals(10, documentDao.getDocumentsOfFolder(documentRoot).size());
    }

    @Test
    public void GetDocumentStream() throws IOException {
        for (Document document : documentDao.getDocumentsOfFolder(documentRoot)) {
            register(documentDao.getDocumentStream(document));
        }
    }

    @Test
    public void RemoveNotes() throws Exception {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "div0-div1-sp1-p0";
        String text = "sun ullakosta ottaa";

        DocumentNote documentNote = documentNoteService.attachNote(createNote(), document, 
                new SelectedText(PREFIX + element, PREFIX + element, 1, 4, text));
        documentNoteService.removeDocumentNotes(document, new DocumentNote[] { documentNote });

        String content = getContent(document.getPath(), -1);
        assertFalse(content
                .contains(start(documentNote.getId()) + text + end(documentNote.getId())));
    }

    @Test
    public void RemoveNotes_Several() throws Exception {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "div0-div1-sp1-p0";
        String text = "sun ullakosta ottaa";
        String text2 = "ottaa";
        String text3 = "ullakosta";

        DocumentNote noteRev = documentNoteService.attachNote(createNote(), document, 
                new SelectedText(PREFIX + element, PREFIX + element, 1, 4, text));
        // note2 won't be removed
        DocumentNote noteRev2 = documentNoteService.attachNote(createNote(), document, 
                new SelectedText(PREFIX + element, PREFIX + element, 1, 4, text2));
        DocumentNote noteRev3 = documentNoteService.attachNote(createNote(), document, 
                new SelectedText(PREFIX + element, PREFIX + element, 1, 2, text3));
        documentNoteService.removeDocumentNotes(document, new DocumentNote[] { noteRev, noteRev3 });

        String content = getContent(document.getPath(), -1);
        assertFalse(content.contains(start(noteRev.getId()) + text + end(noteRev.getId())));
        assertTrue(content.contains(start(noteRev2.getId()) + text2 + end(noteRev2.getId())));
        assertFalse(content.contains(start(noteRev3.getId()) + text3 + end(noteRev3.getId())));
    }

    @Test
    public void UpdateNote() throws Exception {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "div0-div1-sp1-p0";
        String text = "sun ullakosta ottaa";

        DocumentNote noteRevision = documentNoteService.attachNote(createNote(), document, 
                new SelectedText(PREFIX + element, PREFIX + element, 1, 4, text));

        String newText = "sun ullakosta";
        documentNoteService.updateNote(noteRevision, 
                new SelectedText(PREFIX + element, PREFIX + element, 1, 2, newText));

        String content = getContent(document.getPath(), -1);
        Long localId = noteRevision.getId();
        assertFalse(content.contains(start(localId) + text + end(localId)));
        assertTrue(content.contains(start(localId) + newText + end(localId)));
    }

    @Test
    public void UpdateNote2() throws IOException, NoteAdditionFailedException {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "div0-div1-sp2-p0";
        String text = "\u00E4st";

        DocumentNote documentNote = documentNoteService.attachNote(createNote(), document, 
                new SelectedText(PREFIX + element, PREFIX + element, 1, 1, text));

        // T-äst-ä
        String newText = "T\u00E4st\u00E4";
        documentNoteService.updateNote(documentNote, 
                new SelectedText(PREFIX + element, PREFIX + element, 0, 2, newText));

        String content = getContent(document.getPath(), -1);
        Long id = documentNote.getId();
        // System.out.println(content);
        assertFalse(content.contains(start(id) + text + end(id)));
        assertTrue(content.contains(start(id) + newText + end(id)));
        // Täst<anchor xml:id="start1266836640612"/>ä<anchor xml:id="end1266836640612"/> rientää
    }

    @Test
    @Transactional
    public void UpdateNote_With_Publishable_State() throws IOException, NoteAdditionFailedException {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "div0-div1-sp2-p0";
        String text = "\u00E4st";

        DocumentNote documentNote = documentNoteService.attachNote(createNote(), document, 
                new SelectedText(PREFIX + element, PREFIX + element, 1, 1, text));
        documentNote.setPublishable(true);

        String newText = "T\u00E4st\u00E4";
        DocumentNote updatedRevision = documentNoteService.updateNote(documentNote, 
                new SelectedText(PREFIX + element, PREFIX + element, 0, 2, newText));
        assertTrue(updatedRevision.isPublishable());
    }

    @Test(expected = RuntimeException.class)
    public void Remove() throws Exception {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        InputStream stream = documentDao.getDocumentStream(document);
        assertNotNull(stream);
        IOUtils.closeQuietly(stream);
        documentDao.remove(document);
        documentDao.getDocumentStream(document);
    }

    @Test(expected = RuntimeException.class)
    public void Remove_All() throws IOException {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        documentDao.removeAll(Collections.singleton(document));
        documentDao.getDocumentStream(document);
    }

    private Note createNote() {
        Note note = new Note();
        if (extendedTerm) {
            note.setTerm(new Term());
        }
        return note;
    }

    @Test(expected = VersioningException.class)
    public void Renamed_File_Is_No_Longer_Available_With_Old_Name() throws IOException {
        Document document = getDocument("/letters/letter_to_the_editor.xml");
        documentDao.rename(document.getId(), "letter_to_the_reader.xml");
        // Path is set to original due to Hibernate's magic updates...
        document.setPath(documentRoot + "/letters/letter_to_the_editor.xml");
        documentDao.getDocumentStream(document);
    }

    @Test
    public void Renamed_File_Is_Available_In_New_Location() throws IOException {
        Document document = getDocument("/letters/letter_to_the_editor.xml");
        documentDao.rename(document.getId(), "letter_to_the_reader.xml");
        Document renamedDocument = getDocument("/letters/letter_to_the_reader.xml");
        assertNotNull(documentDao.getDocumentStream(renamedDocument));
    }

    @Test
    public void From_Root_Path() {
        assertEquals(8, documentDao.fromPath(documentRoot, null).size());
    }

    @Test
    public void From_Nested_Path() {
        assertEquals(3, documentDao.fromPath(documentRoot + "/letters", null).size());
    }

    @Test
    public void Note_Comments_Of_Document() throws Exception {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "div0-div1-sp1-p0";
        String text = "sun ullakosta ottaa";

        DocumentNote docNote = documentNoteService.attachNote(createNote(), document,
                new SelectedText(PREFIX + element, PREFIX + element, 1, 4, text));
        noteDao.createComment(docNote.getNote(), "Yay");
        List<NoteComment> comments = documentDao.getNoteComments(document.getId(), 3);
        assertFalse(comments.isEmpty());
    }
}
