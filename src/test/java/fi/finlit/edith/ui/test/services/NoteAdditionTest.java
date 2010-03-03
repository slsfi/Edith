/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static fi.finlit.edith.ui.services.DocumentRepositoryImpl.extractName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

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
import fi.finlit.edith.ui.services.ElementContext;

/**
 * NoteAdditionTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteAdditionTest extends AbstractServiceTest{

    @Inject @Symbol(ServiceTestModule.TEST_DOCUMENT_CONTENT_KEY)
    private String testDocumentContent;

    @Inject @Autobuild
    private DocumentRepositoryImpl documentRepo;

    private Reader source;

    private ByteArrayOutputStream target;

    private String localId;

    private final Random random = new Random();

    @Before
    public void setUp() throws SVNException, IOException, XMLStreamException{
        source = new StringReader(testDocumentContent);
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
//        System.out.println(content);
        assertTrue(content.contains("h" + start(localId) + "uone</ref>: per" + end(localId) + "\u00E4ll\u00E4"));
        assertTrue(content.contains("samalla " + start(localId) + "sivulla <ref xml:id=\"ref.4\" target=\"note.4\">ra" + end(localId) + "hi</ref> ja siin"));
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
    public void addNote_on_top_of_another_note_in_child() throws Exception {
        String startElement = "play-act-stage-ref";
        String endElement = "play-act-stage";
        String text = "ne\n: per\u00E4";
        addNote(new SelectedText(startElement, endElement, text));

        String text2 = "uone\n: per\u00E4ll\u00E4 o";
        ByteArrayOutputStream target2 = new ByteArrayOutputStream();
        XMLEventReader sourceReader = inFactory.createXMLEventReader(new ByteArrayInputStream(target.toByteArray()));
        XMLEventWriter targetWriter = outFactory.createXMLEventWriter(target2);
        documentRepo.addNote(sourceReader, targetWriter, new SelectedText(startElement, endElement, 1, 2, text2), localId);
        String content = new String(target2.toByteArray(), "UTF-8");
        System.out.println(content);
        fail("Create a cool assertion!");
    }

    @Test
    public void addNote_verify_subelement_not_eaten() throws Exception {
        String element = "play-act-stage";
        String text = "Topi";
        addNote(new SelectedText(element, element, text));

        assertTrue(testDocumentContent.contains("<ref xml:id=\"ref.4\" target=\"note.4\">rahi</ref>"));
        String content = getContent();
//        System.out.println(content);
        assertTrue(content.contains(start(localId) + text + end(localId)));
        assertTrue(content.contains("<ref xml:id=\"ref.4\" target=\"note.4\">rahi</ref>"));
    }

    @Test
    public void generic_selections() throws Exception {
        for (SelectedText sel : createSelections()) {
            System.out.println(sel);
        }
    }
    
    private List<SelectedText> createSelections() throws Exception {
        // TODO Requirements
        // Generate SelectedText instances
        // For each characters block one selection
        // For consecutive elements one selection

        // TODO Create test material
        // Go through event stream
        // Update context
        // Create SelectedText instances for the required cases

        // TODO Run tests
        // Clean run
        // After that chained run
        // Record failures and report in the end

        List<SelectedText> selections = new ArrayList<SelectedText>();
        ElementContext context = new ElementContext(3);
        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(source);
        String prevCharacters = null;
        String prevContext = null;
        try {
            while (reader.hasNext()) {
                XMLEvent e = reader.nextEvent();
                if (e.isStartElement()) {
                    context.push(extractName(e.asStartElement()));
                } else if (e.isEndElement()) {
                    context.pop();
                } else if (e.isCharacters()) {
                    // TODO : precompile regex
                    String characters = e.asCharacters().getData().replaceAll("\\s+", " ").trim();
                    if (characters.length() == 0) {
                        continue;
                    }

                    // Generate character block selection
                    SelectedText singleElementSelection = createSingleElementSelectedText(characters, context);
                    if (singleElementSelection != null) {
                        selections.add(singleElementSelection);
                    }

                    // Generate character block to next character selection
                    if (prevCharacters == null) {
                        prevCharacters = characters;
                        continue;
                    }

                    SelectedText multipleElementSelection = createMultipleElementSelectedText(prevCharacters, characters, prevContext, context);
                    selections.add(multipleElementSelection);

                    prevCharacters = characters;
                    prevContext = context.getPath();
                }
            }
        } finally {
            reader.close();
        }
        return selections;
    }

    // TODO Combine logic?
    private SelectedText createSingleElementSelectedText(String characters, ElementContext context) {
        int min = generateRandomNumber(0, characters.length());
        int max = generateRandomNumber(min, characters.length());
        String selection = characters.substring(min, max);
        String startId = context.getPath();
        String endId = context.getPath();
        String words[] = StringUtils.split(selection);
        if (words.length < 1) {
            return null;
        }
        String firstWord = words[0];
        String lastWord = words[words.length - 1];
        int startIndex = findStartIndex(characters, firstWord, min);
        int endIndex = findEndIndex(characters, lastWord, max);
        if (startIndex <= 0 || endIndex <= 0) {
            throw new RuntimeException("Couldn't find occurrences!");
        }

        return new SelectedText(startId, endId, startIndex, endIndex, selection);
    }

    private SelectedText createMultipleElementSelectedText(String prevCharacters, String characters, String prevContext, ElementContext context) {
        int min = generateRandomNumber(0, prevCharacters.length());
        int max = generateRandomNumber(0, characters.length());
        String startSelection = prevCharacters.substring(min);
        String endSelection = characters.substring(0, max);
        String startId = prevContext;
        String endId = context.getPath();
        String startWords[] = StringUtils.split(startSelection);
        String endWords[] = StringUtils.split(endSelection);
        if (startWords.length < 1 || endWords.length < 1) {
            return null;
        }
        String firstWord = startWords[0];
        String lastWord = endWords[endWords.length - 1];
        int startIndex = findStartIndex(prevCharacters, firstWord, min);
        int endIndex = findEndIndex(characters, lastWord, max);
        if (startIndex <= 0 || endIndex <= 0) {
            throw new RuntimeException("Couldn't find occurrences!");
        }

        return new SelectedText(startId, endId, startIndex, endIndex, startSelection + endSelection);
    }

    private int generateRandomNumber(int max, int min) {
        return random.nextInt(min - max + 1) + max;
    }

    private int findStartIndex(String string, String word, int offset) {
        return StringUtils.countMatches(string.substring(0, (offset + word.length()) + 1 > string.length() ? (offset + word.length()) : (offset + word.length()) + 1), word);
    }

    private int findEndIndex(String string, String word, int offset) {
        return StringUtils.countMatches(string.substring(0, offset), word);
    }

    @Override
    protected Class<?> getServiceClass() {
        return null;
    }

}
