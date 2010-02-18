/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

import fi.finlit.edith.domain.SelectedText;
import fi.finlit.edith.ui.services.DocumentRepositoryImpl;

/**
 * TEIManipulationTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteAdditionTest extends AbstractServiceTest{

    @Inject @Symbol(ServiceTestModule.TEST_DOCUMENT_FILE_KEY)
    private String testDocument;

    @Autobuild
    @Inject
    private DocumentRepositoryImpl documentRepo;

    private InputStream source;

    private File targetFile;

    private OutputStream target;

    private String localId;

    private XMLEventReader sourceReader;

    private XMLEventWriter targetWriter;

    @Before
    public void setUp() throws SVNException, IOException, XMLStreamException{
        source = new FileInputStream(new File(testDocument));
        targetFile = File.createTempFile("test", null);
        target = new FileOutputStream(targetFile);
        localId = UUID.randomUUID().toString();

        sourceReader = XMLInputFactory.newInstance().createXMLEventReader(source);
        targetWriter = XMLOutputFactory.newInstance().createXMLEventWriter(target);
    }

    @After
    public void tearDown() throws Exception {
        if (targetFile != null) {
            targetFile.delete();
        }
        source.close();
        target.close();
    }

    @Test
    public void addNote_for_p() throws Exception{
        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";
        documentRepo.addNote(sourceReader, targetWriter, new SelectedText(element, element, text), localId);

        String content = FileUtils.readFileToString(targetFile, "UTF-8");
        assertTrue(content.contains("k\u00E4ski " + start(localId) + text + end(localId) + " p\u00E4\u00E4lles"));
    }

    @Test
    public void addNote_for_speaker() throws Exception{
        String element = "play-act-sp-speaker";
        String text = "Esko.";
        documentRepo.addNote(sourceReader, targetWriter, new SelectedText(element, element, text), localId);

        String content = FileUtils.readFileToString(targetFile, "UTF-8");
        assertTrue(content.contains("<speaker>" + start(localId) + text + end(localId) + "</speaker>"));
    }

    @Test
    public void addNote_multiple_elements() throws Exception{
        String start = "play-act-sp2-p";
        String end = "play-act-sp3-speaker";
        String text = "ja polvip\u00F6ksyt. Esko.";
        documentRepo.addNote(sourceReader, targetWriter, new SelectedText(start, end, text), localId);

        String content = FileUtils.readFileToString(targetFile, "UTF-8");
        assertTrue(content.contains(start(localId) + "ja polvip\u00F6ksyt."));
        assertTrue(content.contains("Esko." + end(localId)));
    }

    @Test
    public void addNote_multiple_elements_2() throws Exception{
        String start = "play-act-sp2-p";
        String end = "play-act-sp3-p";
        String text = "ja polvip\u00F6ksyt. Esko. (panee ty\u00F6ns\u00E4 pois).";
        documentRepo.addNote(sourceReader, targetWriter, new SelectedText(start, end, text), localId);

        String content = FileUtils.readFileToString(targetFile, "UTF-8");
        assertTrue(content.contains(start(localId) + "ja polvip\u00F6ksyt."));
        assertTrue(content.contains("(panee ty\u00F6ns\u00E4 pois)." + end(localId)));
    }

    @Test
    public void addNote_long() throws Exception{
        String element = "play-act-sp-p";
        StringBuilder text = new StringBuilder();
        text.append("matkalle, nimitt\u00E4in h\u00E4\u00E4retkelleni, itsi\u00E4ni sonnustan, ");
        text.append("ja sulhais-vaatteisin puettuna olen, koska h\u00E4n takaisin pal");
        documentRepo.addNote(sourceReader, targetWriter, new SelectedText(element, element, text.toString()), localId);

        String content = FileUtils.readFileToString(targetFile, "UTF-8");
        System.out.println(content);
        assertTrue(content.contains(start(localId) + "matkalle, nimitt\u00E4in"));
        assertTrue(content.contains(" takaisin pal" + end(localId)));
    }

    @Test
    public void addNote_short_note_1() throws Exception {
        String element = "play-act-stage";
        String text = "es";
        documentRepo.addNote(sourceReader, targetWriter, new SelectedText(element, element, 1, 1,
                text.toString()), localId);

        String content = FileUtils.readFileToString(targetFile, "UTF-8");
        System.out.println(content);
        assertTrue(content.contains("ed" + start(localId) + "es" + end(localId) + "s\u00E4"));
    }

    @Test
    public void addNote_short_note_2() throws Exception {
        String element = "play-act-stage";
        String text = "es";
        documentRepo.addNote(sourceReader, targetWriter, new SelectedText(element, element, 2, 2,
                text.toString()), localId);

        String content = FileUtils.readFileToString(targetFile, "UTF-8");
        System.out.println(content);
        assertTrue(content.contains("\u00E4\u00E4r" + start(localId) + "es" + end(localId) + "s\u00E4,"));
    }

    @Test
    public void addNote_short_note_3() throws Exception {
        String element = "play-act-stage";
        String text = "es";
        documentRepo.addNote(sourceReader, targetWriter, new SelectedText(element, element, 3, 3,
                text.toString()), localId);

        String content = FileUtils.readFileToString(targetFile, "UTF-8");
        System.out.println(content);
        assertTrue(content.contains("vier" + start(localId) + "es" + end(localId) + "s\u00E4,"));
    }

    @Test
    public void addNote_line_breaks_in_selection() throws Exception {
        String startElement = "play-description-castList-castItem8-roleDesc";
        String endElement = "play-description-castList-castItem9-roleDesc";
        String text = " \nori sepp\u00E4\n.\nKarri\n,\ntalon";
        documentRepo.addNote(sourceReader, targetWriter, new SelectedText(startElement, endElement, 1, 1,
                text), localId);
        String content = FileUtils.readFileToString(targetFile, "UTF-8");
        System.out.println(content);
        assertTrue(content.contains("nu" + start(localId) + "ori"));
        assertTrue(content.contains("talon" + end(localId) + "is\u00E4nt\u00E4"));
    }

    @Override
    protected Class<?> getServiceClass() {
        return null;
    }

}
