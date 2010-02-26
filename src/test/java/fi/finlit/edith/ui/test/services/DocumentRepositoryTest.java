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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
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
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;
import fi.finlit.edith.domain.SelectedText;
import fi.finlit.edith.ui.services.AdminService;
import fi.finlit.edith.ui.services.NoteAdditionFailedException;
import fi.finlit.edith.ui.services.svn.SubversionService;

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
    private NoteRepository noteRepo;

    @Inject
    private NoteRevisionRepository noteRevisionRepo;

    @Inject
    private SubversionService subversionService;

    @Inject
    private AdminService adminService;

    @Inject
    @Symbol(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;

    private String getContent(String svnPath, long svnRevision) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = register(subversionService.getStream(svnPath, svnRevision));
        IOUtils.copy(in, out);
        in.close();
        out.close();
        return new String(out.toByteArray(), "UTF-8");
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws IOException{
        closeStreams();
        adminService.removeNotesAndTerms();
        subversionService.destroy();
        subversionService.initialize();
    }

    @Test
    public void getAll() {
        assertEquals(6, documentRepo.getAll().size());
    }

    @Test
    public void getDocumentsOfFolder() {
        assertEquals(6, documentRepo.getDocumentsOfFolder(documentRoot).size());
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
        assertFalse(documentRepo.getRevisions(document).isEmpty());
    }

    @Test
    public void getRevisions() {
        for (Document document : documentRepo.getAll()) {
            assertFalse(documentRepo.getRevisions(document).isEmpty());
        }
    }

    private Document getDocument(String path){
        return documentRepo.getDocumentForPath(documentRoot + path);
    }

    @Test
    public void addNote() throws Exception {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";

        NoteRevision note = documentRepo.addNote(document.getRevision(-1), new SelectedText(element, element, text));

        String content = getContent(document.getSvnPath(), -1);
        String localId = note.getRevisionOf().getLocalId();
        assertTrue(content.contains(start(localId) + text + end(localId)));
    }

    @Test
    public void addNote2() throws IOException, NoteAdditionFailedException{
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
//        act1-sp4 - act1-sp4 : minä; ja nytpä, luulen,
        String element = "play-act-sp4-p";
        String text = "min\u00E4; ja nytp\u00E4, luulen,";

        NoteRevision note = documentRepo.addNote(document.getRevision(-1), new SelectedText(element, element, text));
        assertNotNull(note);
    }

    @Test
    public void removeNotes() throws Exception {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";

        NoteRevision noteRev = documentRepo.addNote(document.getRevision(-1), new SelectedText(element, element, text));
        Note note = noteRev.getRevisionOf();
        documentRepo.removeNotes(document.getRevision(-1), new Note[] { note });

        String content = getContent(document.getSvnPath(), -1);
        assertFalse(content.contains(start(note.getLocalId()) + text + end(note.getLocalId())));
    }

    @Test
    public void addRemoveNote() throws IOException, NoteAdditionFailedException{
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        int count = noteRevisionRepo.queryNotes("*").getAvailableRows();

        // add
        String element = "play-act-sp4-p";
        String text = "min\u00E4; ja nytp\u00E4, luulen,";
        NoteRevision note = documentRepo.addNote(document.getRevision(-1), new SelectedText(element, element, text));

        assertEquals(count+1, noteRevisionRepo.queryNotes("*").getAvailableRows());
        int countInDoc = noteRevisionRepo.getOfDocument(document.getRevision(note.getSvnRevision())).size();

        // remove
        documentRepo.removeNotes(document.getRevision(note.getSvnRevision()), note.getRevisionOf());
        Note deletedNote = noteRepo.getById(note.getRevisionOf().getId());
        assertTrue(deletedNote.getLatestRevision().isDeleted());

        GridDataSource dataSource = noteRevisionRepo.queryNotes("*");
        int available = dataSource.getAvailableRows();
        dataSource.prepare(0, available-1, Collections.<SortConstraint>emptyList());
        for (int i = 0; i < available; i++){
            NoteRevision rev = (NoteRevision) dataSource.getRowValue(i);
            assertEquals(rev, rev.getRevisionOf().getLatestRevision());
        }

        long svnRevision = deletedNote.getLatestRevision().getSvnRevision();
        assertEquals(countInDoc - 1, noteRevisionRepo.getOfDocument(document.getRevision(svnRevision)).size());
        assertEquals(count, available);
    }

    @Test
    public void removeNotes_several() throws Exception {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
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

        String content = getContent(document.getSvnPath(), -1);
        assertFalse(content.contains(start(note.getLocalId()) + text + end(note.getLocalId())));
        assertTrue(content.contains(start(note2.getLocalId()) + text2 + end(note2.getLocalId())));
        assertFalse(content.contains(start(note3.getLocalId()) + text3 + end(note3.getLocalId())));
    }

    @Test
    public void updateNote() throws Exception {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";

        NoteRevision noteRevision = documentRepo.addNote(document.getRevision(-1), new SelectedText(element, element, text));

        String newText = "sun ullakosta";
        documentRepo.updateNote(noteRevision, new SelectedText(element, element, newText));

        String content = getContent(document.getSvnPath(), -1);
        String localId = noteRevision.getRevisionOf().getLocalId();
        assertFalse(content.contains(start(localId) + text + end(localId)));
        assertTrue(content.contains(start(localId) + newText + end(localId)));
    }

    @Test
    public void updateNote2() throws IOException, NoteAdditionFailedException{
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "play-act-sp3-p";
        String text = "\u00E4st";

        NoteRevision noteRevision = documentRepo.addNote(document.getRevision(-1), new SelectedText(element, element, text));

        //T-äst-ä
        String newText = "T\u00E4st\u00E4";
        documentRepo.updateNote(noteRevision, new SelectedText(element, element, newText));

        String content = getContent(document.getSvnPath(), -1);
        String localId = noteRevision.getRevisionOf().getLocalId();
//        System.out.println(content);
        assertFalse(content.contains(start(localId) + text + end(localId)));
        assertTrue(content.contains(start(localId) + newText + end(localId)));
        // Täst<anchor xml:id="start1266836640612"/>ä<anchor xml:id="end1266836640612"/> rientää
    }

    @Test
    public void addNote_twice_overlapping() throws IOException, NoteAdditionFailedException{
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "play-act-sp3-p";
        String text = "\u00E4st";

        NoteRevision noteRevision = documentRepo.addNote(document.getRevision(-1), new SelectedText(element, element, text));

        //T-äst-ä
        String newText = "T\u00E4st\u00E4";
        NoteRevision noteRevision2 = documentRepo.addNote(document.getRevision(noteRevision.getSvnRevision()), new SelectedText(element, element, newText));

        String content = getContent(document.getSvnPath(), -1);
        String localId = noteRevision.getRevisionOf().getLocalId();
        String localId2 = noteRevision2.getRevisionOf().getLocalId();
        assertTrue(content.contains(start(localId2) + "T" + start(localId) + text + end(localId) + "\u00E4" + end(localId2)));
    }

    @Test
    public void addNote_twice_overlapping2() throws IOException, NoteAdditionFailedException{
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String startElement = "play-description-castList-castItem7-role";
        String endElement = "play-description-castList-castItem8-roleDesc";
        String text = "\na\n,\nh\u00E4nen tytt\u00E4rens\u00E4, Topiaksen hoitolapsi\n.\n \nKristo\n,\nn";

        NoteRevision noteRevision = documentRepo.addNote(document.getRevision(-1), new SelectedText(startElement, endElement, 3, 1, text));

        String newText = "\nna\n,\nh\u00E4nen tytt\u00E4rens\u00E4, Topiaksen hoitolapsi\n.\n \nKristo\n,\nnuori s";
        NoteRevision noteRevision2 = documentRepo.addNote(document.getRevision(noteRevision.getSvnRevision()), new SelectedText(startElement, endElement, 1, 1, newText));

        String content = getContent(document.getSvnPath(), -1);
        String localId = noteRevision.getRevisionOf().getLocalId();
        String localId2 = noteRevision2.getRevisionOf().getLocalId();
//        System.out.println(content);
        assertTrue(content.contains("Jaa" + start(localId2) + "n" + start(localId) + "a</role>, <roleDesc>h\u00E4nen tytt\u00E4rens\u00E4, Topiaksen\n"));
        assertTrue(content.contains("<castItem><role>Kristo</role>, <roleDesc>n" + end(localId) + "uori s" + end(localId2) + "epp\u00E4</roleDesc>.</castItem>"));
    }

    @Test
    public void addNote_role_description() throws IOException, NoteAdditionFailedException {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String startElement = "play-description-castList-castItem7-role";
        String endElement = "play-description-castList-castItem8-roleDesc";
        String text = "\na\n,\nh\u00E4nen tytt\u00E4rens\u00E4, Topiaksen hoitolapsi\n.\n \nKristo\n,\nn";

        NoteRevision noteRevision = documentRepo.addNote(document.getRevision(-1), new SelectedText(startElement, endElement, 3, 1, text));

        String newText = "\nna\n,\nh\u00E4nen tytt\u00E4rens\u00E4, Topiaksen hoitolapsi\n.\n \nKristo\n,\nnuori s";
        NoteRevision noteRevision2 = documentRepo.addNote(document.getRevision(noteRevision.getSvnRevision()), new SelectedText(startElement, endElement, 1, 1, newText));

        String content = getContent(document.getSvnPath(), -1);
        String localId = noteRevision.getRevisionOf().getLocalId();
        String localId2 = noteRevision2.getRevisionOf().getLocalId();
        System.out.println(content);
        assertTrue(content.contains("Jaa" + start(localId2) + "n" + start(localId) + "a</role>, <roleDesc>h\u00E4nen tytt\u00E4rens\u00E4, Topiaksen\n"));
        assertTrue(content.contains("<castItem><role>Kristo</role>, <roleDesc>n" + end(localId) + "uori s" + end(localId2) + "epp\u00E4</roleDesc>.</castItem>"));
    }

    @Test
    public void removeAllNotes() throws Exception{
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";

        NoteRevision noteRevision = documentRepo.addNote(document.getRevision(-1), new SelectedText(element, element, text));
        DocumentRevision docRevision = noteRevision.getDocumentRevision();

        List<NoteRevision> revs = noteRevisionRepo.getOfDocument(docRevision);
        assertTrue(revs.size() > 0);

        docRevision = documentRepo.removeAllNotes(document);
        revs = noteRevisionRepo.getOfDocument(docRevision);
        assertTrue(revs.isEmpty());
    }

    @Override
    protected Class<?> getServiceClass() {
        return DocumentRepository.class;
    }

}
