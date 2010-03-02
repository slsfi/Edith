/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
 * NoteAdditionTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteAdditionTest extends AbstractServiceTest{

    @Inject @Symbol(ServiceTestModule.TEST_DOCUMENT_FILE_KEY)
    private String testDocument;

    // TODO : expose testDocument content as byte array into registry (key : TEST_DOCUMENT_CONTENT_KEY)

    @Autobuild
    @Inject
    private DocumentRepositoryImpl documentRepo;

    private InputStream source;

    private ByteArrayOutputStream target;

    private String localId;

    @Before
    public void setUp() throws SVNException, IOException, XMLStreamException{
        source = new FileInputStream(new File(testDocument));
        target = new ByteArrayOutputStream();
        localId = UUID.randomUUID().toString();
    }

    private String getContent() throws UnsupportedEncodingException{
        return new String(target.toByteArray(), "UTF-8");
    }

    @After
    public void tearDown() throws Exception {
        source.close();
        target.close();
    }

    private void addNote(SelectedText selectedText) throws Exception{
        XMLEventReader sourceReader = inFactory.createXMLEventReader(source);
        XMLEventWriter targetWriter = outFactory.createXMLEventWriter(target);
        documentRepo.addNote(sourceReader, targetWriter, selectedText, localId);
    }

    @Test
    public void addNote_for_p() throws Exception{
        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";
        addNote(new SelectedText(element, element, text));

        String content = getContent();
        //System.out.println(content);
        assertTrue(content.contains("k\u00E4ski " + start(localId) + text + end(localId) + " p\u00E4\u00E4lles"));
    }

    @Test
    public void addNote_for_speaker() throws Exception{
        String element = "play-act-sp-speaker";
        String text = "Esko.";
        addNote(new SelectedText(element, element, text));

        String content = getContent();
        assertTrue(content.contains("<speaker>" + start(localId) + text + end(localId) + "</speaker>"));
    }

    @Test
    public void addNote_multiple_elements() throws Exception{
        String start = "play-act-sp2-p";
        String end = "play-act-sp3-speaker";
        String text = "ja polvip\u00F6ksyt. Esko.";
        addNote(new SelectedText(start, end, text));

        String content = getContent();
        assertTrue(content.contains(start(localId) + "ja polvip\u00F6ksyt."));
        assertTrue(content.contains("Esko." + end(localId)));
    }

    @Test
    public void addNote_multiple_elements_2() throws Exception{
        String start = "play-act-sp2-p";
        String end = "play-act-sp3-p";
        String text = "ja polvip\u00F6ksyt. Esko. (panee ty\u00F6ns\u00E4 pois).";
        addNote(new SelectedText(start, end, text));

        String content = getContent();
        assertTrue(content.contains(start(localId) + "ja polvip\u00F6ksyt."));
        assertTrue(content.contains("(panee ty\u00F6ns\u00E4 pois)." + end(localId)));
    }

    @Test
    public void addNote_long() throws Exception{
        String element = "play-act-sp-p";
        StringBuilder text = new StringBuilder();
        text.append("matkalle, nimitt\u00E4in h\u00E4\u00E4retkelleni, itsi\u00E4ni sonnustan, ");
        text.append("ja sulhais-vaatteisin puettuna olen, koska h\u00E4n takaisin pal");
        addNote(new SelectedText(element, element, text.toString()));

        String content = getContent();
//        System.out.println(content);
        assertTrue(content.contains(start(localId) + "matkalle, nimitt\u00E4in"));
        assertTrue(content.contains(" takaisin pal" + end(localId)));
    }

    @Test
    public void addNote_short_note_1() throws Exception {
        String element = "play-act-stage";
        String text = "es";
        addNote(new SelectedText(element, element, 1, 1, text.toString()));

        String content = getContent();
        //System.out.println(content);
        assertTrue(content.contains("ed" + start(localId) + "es" + end(localId) + "s\u00E4"));
    }

    @Test
    public void addNote_short_note_2() throws Exception {
        String element = "play-act-stage";
        String text = "es";
        addNote(new SelectedText(element, element, 2, 2, text));

        String content = getContent();
        assertTrue(content.contains("\u00E4\u00E4r" + start(localId) + "es" + end(localId) + "s\u00E4,"));
    }

    @Test
    public void addNote_short_note_3() throws Exception {
        String element = "play-act-stage";
        String text = "es";
        addNote(new SelectedText(element, element, 3, 3, text));

        String content = getContent();
        assertTrue(content.contains("vier" + start(localId) + "es" + end(localId) + "s\u00E4,"));
    }

    @Test
    public void addNote_one_char() throws Exception {
        String element = "play-act-stage";
        String text = "i";
        addNote(new SelectedText(element, element, 12, 12, text));

        String content = getContent();
//        System.out.println(content);
        assertTrue(content.contains("v" + start(localId) + "i" + end(localId) + "eress\u00E4,"));
    }

    @Test
    public void addNote_line_breaks_in_selection() throws Exception {
        String startElement = "play-description-castList-castItem8-roleDesc";
        String endElement = "play-description-castList-castItem9-roleDesc";
        String text = " \nori sepp\u00E4\n.\nKarri\n,\ntalon";
        addNote(new SelectedText(startElement, endElement, 1, 1, text));

        String content = getContent();
        assertTrue(content.contains("nu" + start(localId) + "ori"));
        assertTrue(content.contains("talon" + end(localId) + "is\u00E4nt\u00E4"));
    }

    @Test
    public void addNote_start_element_inside_end_element() throws Exception {
        String startElement = "play-act-stage-ref";
        String endElement = "play-act-stage";
        String text = "uone\n: per";
        addNote(new SelectedText(startElement, endElement, text));

        String content = getContent();
//        System.out.println(content);
        assertTrue(content.contains("h" + start(localId) + "uone</ref>: per" + end(localId) + "\u00E4ll\u00E4"));
    }

    @Test
    public void addNote_start_element_inside_end_element_end_does_not_escape() throws Exception {
        String startElement = "play-act-stage-ref";
        String endElement = "play-act-stage";
        String text = "uone\n: p";
        addNote(new SelectedText(startElement, endElement, 1, 2, text));

        String content = getContent();
//        System.out.println(content);
        assertTrue(content.contains("h" + start(localId) + "uone</ref>: p" + end(localId) + "er\u00E4ll\u00E4"));
    }

    @Test
    public void addNote_end_element_inside_start_element() throws Exception {
        String startElement = "play-act-stage";
        String endElement = "play-act-stage-ref";
        String text = "piaksen huo";
        addNote(new SelectedText(startElement, endElement, text));

        String content = getContent();
//        System.out.println(content);
        assertTrue(content.contains("(To" + start(localId) + "piaksen <ref xml:id=\"ref.3\" target=\"note.3\">huo" + end(localId) + "ne"));
        assertEquals(1, StringUtils.countMatches(content, "Jaana istuu pöydän ääressä, kutoen sukkaa,"));
    }

    @Test
    public void addNote_start_element_inside_end_element_and_end_element_inside_start_element() throws Exception {
        String startElement = "play-act-stage-ref";
        String endElement = "play-act-stage";
        String text = "uone\n: per";
        addNote(new SelectedText(startElement, endElement, text));

        String startElement2 = "play-act-stage";
        String endElement2 = "play-act-stage-ref2";
        String text2 = "sivulla ra";
        ByteArrayOutputStream target2 = new ByteArrayOutputStream();
        XMLEventReader sourceReader = inFactory.createXMLEventReader(new ByteArrayInputStream(target.toByteArray()));
        XMLEventWriter targetWriter = outFactory.createXMLEventWriter(target2);
        documentRepo.addNote(sourceReader, targetWriter, new SelectedText(startElement2, endElement2, text2), localId);
        String content = new String(target2.toByteArray(), "UTF-8");
        assertTrue(content.contains("h" + start(localId) + "uone</ref>: p" + end(localId) + "er\u00E4ll\u00E4"));
    }

    @Test
    public void addNote_end_element_inside_start_element_and_start_element_inside_end_element_() throws Exception {
        String startElement = "play-act-stage";
        String endElement = "play-act-stage-ref2";
        String text = "sivulla ra";
        addNote(new SelectedText(startElement, endElement, text));

        String startElement2 = "play-act-stage-ref";
        String endElement2 = "play-act-stage";
        String text2 = "uone\n: per";
        ByteArrayOutputStream target2 = new ByteArrayOutputStream();
        XMLEventReader sourceReader = inFactory.createXMLEventReader(new ByteArrayInputStream(target.toByteArray()));
        XMLEventWriter targetWriter = outFactory.createXMLEventWriter(target2);
        documentRepo.addNote(sourceReader, targetWriter, new SelectedText(startElement2, endElement2, text2), localId);
        String content = new String(target2.toByteArray(), "UTF-8");
//        System.out.println(content);
        assertTrue(content.contains("h" + start(localId) + "uone</ref>: per" + end(localId) + "\u00E4ll\u00E4"));
    }

    @Test
    public void addNote_verify_subelement_not_eaten() throws Exception {
        String element = "play-act-stage";
        String text = "Topi";
        addNote(new SelectedText(element, element, text));

        String content = FileUtils.readFileToString(new File(testDocument), "UTF-8");
        assertTrue(content.contains("<ref xml:id=\"ref.4\" target=\"note.4\">rahi</ref>"));
        content = getContent();
//        System.out.println(content);
        assertTrue(content.contains(start(localId) + text + end(localId)));
        assertTrue(content.contains("<ref xml:id=\"ref.4\" target=\"note.4\">rahi</ref>"));
    }

    @Override
    protected Class<?> getServiceClass() {
        return null;
    }

}
