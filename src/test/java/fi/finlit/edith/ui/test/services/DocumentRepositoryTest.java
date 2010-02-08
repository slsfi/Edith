/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.SelectedText;
import fi.finlit.edith.ui.services.SubversionService;

/**
 * DocumentRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRepositoryTest extends AbstractServiceTest {

    @Inject
    private DocumentRepository documentRepo;

    @Inject
    private SubversionService subversionService;

    @Inject
    @Symbol(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;

    private List<Document> savedDocs = new ArrayList<Document>();

    private static boolean initialized = false;

    @Before
    public void setUp(){
        if (!initialized){
            subversionService.destroy();
            subversionService.initialize();
            initialized = true;
        }
    }

    @After
    public void tearDown() throws IOException{
        closeStreams();
        for (Document doc : savedDocs){
            documentRepo.remove(doc);
        }
    }

    @Test
    public void getAll() {
        assertEquals(7, documentRepo.getAll().size());
    }

    @Test
    public void getDocumentsOfFolder() {
        assertEquals(7, documentRepo.getDocumentsOfFolder(documentRoot).size());
    }

    @Test
    public void getDocumentForPath() {
        assertNotNull(documentRepo.getDocumentForPath("/documents/" + UUID.randomUUID().toString()));
    }

    @Test
    public void getDocumentStream() throws IOException {
        for (Document document : documentRepo.getAll()) {
            register(documentRepo.getDocumentStream(new DocumentRevision(document, -1)));
        }
    }

    @Test
    public void addDocument() throws IOException{
        File file = File.createTempFile("test", null);
        FileUtils.writeStringToFile(file, "test file", "UTF-8");
        String targetPath = "/documents/" + UUID.randomUUID().toString();
        documentRepo.addDocument(targetPath, file);

        Document document = documentRepo.getDocumentForPath(targetPath);
        savedDocs.add(document);
        assertFalse(documentRepo.getRevisions(document).isEmpty());
    }

    @Test
    public void getRevisions() {
        for (Document document : documentRepo.getAll()) {
            assertFalse(documentRepo.getRevisions(document).isEmpty());
        }
    }

    @Test
    public void addNote() throws Exception {
        Document document = documentRepo.getDocumentForPath(documentRoot
                + "/Nummisuutarit rakenteistettuna.xml");

        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";

        
        NoteRevision note = documentRepo.addNote(document.getRevision(-1), new SelectedText(element, element, text));

        // TODO Resource handling into setup + teardown?
        File tmpFile = File.createTempFile("nummarit", ".xml");
        OutputStream out = new FileOutputStream(tmpFile);
        InputStream in = register(subversionService.getStream(document.getSvnPath(), -1));
        IOUtils.copy(in, out);
        out.close();

        String content = FileUtils.readFileToString(tmpFile, "UTF-8");
        tmpFile.delete();
        String localId = note.getRevisionOf().getLocalId();
        assertTrue(content.contains(start(localId) + text + end(localId)));
    }

    @Test
    public void addNote2() throws IOException{
        Document document = documentRepo.getDocumentForPath(documentRoot
                + "/Nummisuutarit rakenteistettuna.xml");

//        act1-sp4 - act1-sp4 : minä; ja nytpä, luulen,
        String element = "play-act-sp4-p";
        String text = "min\u00E4; ja nytp\u00E4, luulen,";

        NoteRevision note = documentRepo.addNote(document.getRevision(-1), new SelectedText(element, element, text));
        assertNotNull(note);
    }

    @Test
    public void removeNotes() throws Exception {
        Document document = documentRepo.getDocumentForPath(documentRoot
                + "/Nummisuutarit rakenteistettuna.xml");

        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";

        NoteRevision noteRev = documentRepo.addNote(document.getRevision(-1), new SelectedText(element, element, text));
        Note note = noteRev.getRevisionOf();
        documentRepo.removeNotes(document.getRevision(-1), new Note[] { note });

        // TODO Resource handling into setup + teardown?
        File tmpFile = File.createTempFile("nummarit", ".xml");
        OutputStream out = new FileOutputStream(tmpFile);
        InputStream in = register(subversionService.getStream(document.getSvnPath(), -1));
        IOUtils.copy(in, out);
        out.close();

        String content = FileUtils.readFileToString(tmpFile, "UTF-8");
        tmpFile.delete();
        assertFalse(content.contains(start(note.getLocalId()) + text + end(note.getLocalId())));
    }

    @Test
    public void removeNotes_several() throws Exception {
        Document document = documentRepo.getDocumentForPath(documentRoot
                + "/Nummisuutarit rakenteistettuna.xml");

        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";
        String text2 = "ottaa";
        String text3 = "ullakosta";

        NoteRevision noteRev = documentRepo.addNote(document.getRevision(-1), new SelectedText(element, element, text));
        // note2 won't be removed
        NoteRevision noteRev2 = documentRepo.addNote(document.getRevision(-1), new SelectedText( element, element, text2));
        NoteRevision noteRev3 = documentRepo.addNote(document.getRevision(-1), new SelectedText(element, element, text3));
        Note note = noteRev.getRevisionOf();
        Note note2 = noteRev2.getRevisionOf();
        Note note3 = noteRev3.getRevisionOf();
        documentRepo.removeNotes(document.getRevision(-1), new Note[] { note, note3 });

        // TODO Resource handling into setup + teardown?
        File tmpFile = File.createTempFile("nummarit", ".xml");
        OutputStream out = new FileOutputStream(tmpFile);
        InputStream in = register(subversionService.getStream(document.getSvnPath(), -1));
        IOUtils.copy(in, out);
        out.close();

        String content = FileUtils.readFileToString(tmpFile, "UTF-8");
        tmpFile.delete();
        assertFalse(content.contains(start(note.getLocalId()) + text + end(note.getLocalId())));
        assertTrue(content.contains(start(note2.getLocalId()) + text2 + end(note2.getLocalId())));
        assertFalse(content.contains(start(note3.getLocalId()) + text3 + end(note3.getLocalId())));
    }

    @Test
    public void updateNote() throws Exception {
        Document document = documentRepo.getDocumentForPath(documentRoot
                + "/Nummisuutarit rakenteistettuna.xml");

        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";

        NoteRevision noteRevision = documentRepo.addNote(document.getRevision(-1), new SelectedText(element, element, text));

        String newText = "sun ullakosta";
        documentRepo.updateNote(noteRevision, new SelectedText(element, element, newText));

        // TODO Resource handling into setup + teardown?
        File tmpFile = File.createTempFile("nummarit", ".xml");
        OutputStream out = new FileOutputStream(tmpFile);
        InputStream in = register(subversionService.getStream(document.getSvnPath(), -1));
        IOUtils.copy(in, out);
        out.close();

        String content = FileUtils.readFileToString(tmpFile, "UTF-8");
        tmpFile.delete();
        String localId = noteRevision.getRevisionOf().getLocalId();
        assertFalse(content.contains(start(localId) + text + end(localId)));
        assertTrue(content.contains(start(localId) + newText + end(localId)));
    }

    @Override
    protected Class<?> getServiceClass() {
        return DocumentRepository.class;
    }

}
