/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteRepository;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteAdditionFailedException;
import fi.finlit.edith.domain.SelectedText;
import fi.finlit.edith.ui.services.AdminService;
import fi.finlit.edith.ui.services.svn.SubversionService;

/**
 * DocumentRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRepositoryTest extends AbstractServiceTest {

    @Inject
    private DocumentRepository documentRepository;

    @Inject
    private DocumentNoteRepository documentNoteRepository;

    @Inject
    private SubversionService subversionService;

    @Inject
    private AdminService adminService;

    @Inject
    @Symbol(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;

    @Test
    public void addDocument() throws IOException{
        File file = File.createTempFile("test", null);
        FileUtils.writeStringToFile(file, "test file", "UTF-8");
        String targetPath = "/documents/" + UUID.randomUUID().toString();
        documentRepository.addDocument(targetPath, file);

        Document document = documentRepository.getDocumentForPath(targetPath);
        assertFalse(documentRepository.getRevisions(document).isEmpty());
    }

    @Test
    public void addNote() throws Exception {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";

        DocumentNote note = documentRepository.addNote(new Note(), document.getRevision(-1), new SelectedText(element, element, text));

        String content = getContent(document.getSvnPath(), -1);
        String localId = note.getLocalId();
        assertTrue(content.contains(start(localId) + text + end(localId)));
    }

    @Test
    public void addRemoveNote() throws IOException, NoteAdditionFailedException{
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        int count = documentNoteRepository.queryNotes("*").getAvailableRows();

        // add
        String element = "play-act-sp4-p";
        String text = "min\u00E4; ja nytp\u00E4, luulen,";
        DocumentNote documentNote = documentRepository.addNote(new Note(), document.getRevision(-1), new SelectedText(element, element, text));

        assertEquals(count+1, documentNoteRepository.queryNotes("*").getAvailableRows());
        // remove
        documentRepository.removeNotes(document.getRevision(documentNote.getSVNRevision()), documentNote);
        DocumentNote deletedDocumentNote = documentNoteRepository.getByLocalId(document.getRevision(documentNote.getSVNRevision() + 1), documentNote.getLocalId());
        assertNull(deletedDocumentNote);

        GridDataSource dataSource = documentNoteRepository.queryNotes("*");
        int available = dataSource.getAvailableRows();
        dataSource.prepare(0, 1000, new ArrayList<SortConstraint>());
        assertEquals(0, available);
    }

    @Test
    public void Add_Note_With_The_Same_Lemma() throws IOException, NoteAdditionFailedException {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "play-act-sp2-p";
        String text = "s";

        documentRepository.addNote(new Note(), document.getRevision(-1), new SelectedText(element, element, 1, 1, text));
        DocumentNote note2 = documentRepository.addNote(new Note(), document.getRevision(-1), new SelectedText(element, element, 2, 2, text));
        List<DocumentNote> documentNotes = documentNoteRepository.getOfDocument(note2.getDocRevision());
        assertEquals(2, documentNotes.size());
    }

    @Test
    public void getAll() {
        assertEquals(6, documentRepository.getAll().size());
    }

    private String getContent(String svnPath, long svnRevision) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = register(subversionService.getStream(svnPath, svnRevision));
        IOUtils.copy(in, out);
        in.close();
        out.close();
        return new String(out.toByteArray(), "UTF-8");
    }

    private Document getDocument(String path){
        return documentRepository.getDocumentForPath(documentRoot + path);
    }

    @Test
    public void getDocumentForPath() {
        assertNotNull(documentRepository.getDocumentForPath("/documents/" + UUID.randomUUID().toString()));
    }

    @Test
    public void getDocumentsOfFolder() {
        assertEquals(6, documentRepository.getDocumentsOfFolder(documentRoot).size());
    }

    @Test
    public void getDocumentStream() throws IOException {
        for (Document document : documentRepository.getAll()) {
            register(documentRepository.getDocumentStream(new DocumentRevision(document, -1)));
        }
    }

    @Test
    public void getRevisions() {
        for (Document document : documentRepository.getAll()) {
            assertFalse(documentRepository.getRevisions(document).isEmpty());
        }
    }

    @Test
    public void removeAllNotes() throws Exception{
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";

        DocumentNote noteRevision = documentRepository.addNote(new Note(), document.getRevision(-1), new SelectedText(element, element, text));
        DocumentRevision docRevision = noteRevision.getDocumentRevision();

        List<DocumentNote> revs = documentNoteRepository.getOfDocument(docRevision);
        assertTrue(revs.size() > 0);

        docRevision = documentRepository.removeAllNotes(document);
        revs = documentNoteRepository.getOfDocument(docRevision);
        assertTrue(revs.isEmpty());
    }

    @Test
    public void removeNotes() throws Exception {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";

        DocumentNote noteRev = documentRepository.addNote(new Note(), document.getRevision(-1), new SelectedText(element, element, text));
        documentRepository.removeNotes(document.getRevision(-1), new DocumentNote[] { noteRev });

        String content = getContent(document.getSvnPath(), -1);
        assertFalse(content.contains(start(noteRev.getLocalId()) + text + end(noteRev.getLocalId())));
    }

    @Test
    public void removeNotes_several() throws Exception {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";
        String text2 = "ottaa";
        String text3 = "ullakosta";

        DocumentNote noteRev = documentRepository.addNote(new Note(), document.getRevision(-1), new SelectedText(element, element, text));
        // note2 won't be removed
        DocumentNote noteRev2 = documentRepository.addNote(new Note(), document.getRevision(-1), new SelectedText( element, element, text2));
        DocumentNote noteRev3 = documentRepository.addNote(new Note(), document.getRevision(-1), new SelectedText(element, element, text3));
        documentRepository.removeNotes(document.getRevision(-1), new DocumentNote[] { noteRev, noteRev3 });

        String content = getContent(document.getSvnPath(), -1);
        assertFalse(content.contains(start(noteRev.getLocalId()) + text + end(noteRev.getLocalId())));
        assertTrue(content.contains(start(noteRev2.getLocalId()) + text2 + end(noteRev2.getLocalId())));
        assertFalse(content.contains(start(noteRev3.getLocalId()) + text3 + end(noteRev3.getLocalId())));
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() {
        closeStreams();
        adminService.removeNotesAndTerms();
        subversionService.destroy();
        subversionService.initialize();
    }

    @Test
    public void updateNote() throws Exception {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";

        DocumentNote noteRevision = documentRepository.addNote(new Note(), document.getRevision(-1), new SelectedText(element, element, text));

        String newText = "sun ullakosta";
        documentRepository.updateNote(noteRevision, new SelectedText(element, element, newText));

        String content = getContent(document.getSvnPath(), -1);
        String localId = noteRevision.getLocalId();
        assertFalse(content.contains(start(localId) + text + end(localId)));
        assertTrue(content.contains(start(localId) + newText + end(localId)));
    }

    @Test
    public void updateNote2() throws IOException, NoteAdditionFailedException{
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "play-act-sp3-p";
        String text = "\u00E4st";

        DocumentNote noteRevision = documentRepository.addNote(new Note(), document.getRevision(-1), new SelectedText(element, element, text));

        //T-äst-ä
        String newText = "T\u00E4st\u00E4";
        documentRepository.updateNote(noteRevision, new SelectedText(element, element, newText));

        String content = getContent(document.getSvnPath(), -1);
        String localId = noteRevision.getLocalId();
//        System.out.println(content);
        assertFalse(content.contains(start(localId) + text + end(localId)));
        assertTrue(content.contains(start(localId) + newText + end(localId)));
        // Täst<anchor xml:id="start1266836640612"/>ä<anchor xml:id="end1266836640612"/> rientää
    }

    @Test(expected = RuntimeException.class)
    public void Remove() throws Exception {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        InputStream stream = documentRepository.getDocumentStream(document.getRevision(-1));
        assertNotNull(stream);
        IOUtils.closeQuietly(stream);
        documentRepository.remove(document);
        documentRepository.getDocumentStream(document.getRevision(-1));
    }

}
