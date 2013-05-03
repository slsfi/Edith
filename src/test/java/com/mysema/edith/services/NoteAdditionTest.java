/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mysema.edith.EdithTestConstants;
import com.mysema.edith.dto.SelectedText;
import com.mysema.edith.util.ElementContext;
import com.mysema.edith.util.StringUtils;


public class NoteAdditionTest extends AbstractHibernateTest {

    private final static Pattern WHITESPACE = Pattern.compile("\\s+");

    @Inject
    private DocumentDaoImpl documentDao;

    @Inject
    private DocumentXMLDaoImpl documentXMLDao;

    private Long localId;

    private final Random random = new Random(27);

    private Reader source;

    private StringWriter target;

    @Inject @Named(EdithTestConstants.TEST_DOCUMENT_CONTENT_KEY)
    private String testDocumentContent;

    private void addNote(SelectedText selectedText) throws Exception {
        addNote(selectedText, source);
    }

    private void addNote(SelectedText selectedText, /*InputStream reader*/ Reader reader) throws Exception {
        XMLEventReader sourceReader = inFactory.createXMLEventReader(reader);
        XMLEventWriter targetWriter = outFactory.createXMLEventWriter(target);
        documentXMLDao.addNote(sourceReader, targetWriter, selectedText, localId);
    }

    @Before
    public void setUp() {
        source = new StringReader(testDocumentContent);
        target = new StringWriter();
        localId = (long) UUID.randomUUID().hashCode();
    }

    @After
    public void tearDown() throws Exception {
        source.close();
        target.close();
    }
    
    @Test
    public void AddNote_end_element_deeply_inside_start_element() throws Exception {
        String startElement = "play-description-castList-castItem13";
        String endElement = "play-description-castList-castItem13-roleDesc-ref";
        String text = ", kraatari";
        addNote(new SelectedText(startElement, endElement, text));

        String content = getContent();
//         System.out.println(content);
        assertTrue(content.contains("<castItem><role>Antres</role>" + start(localId) + ", <roleDesc><ref xml:id=\"ref.1\" target=\"note.1\">kraatari" + end(localId) + "</ref> ja"));
    }

    @Test
    public void AddNote_end_element_inside_start_element() throws Exception {
        String startElement = "play-act-stage";
        String endElement = "play-act-stage-ref";
        String text = "piaksen huo";
        addNote(new SelectedText(startElement, endElement, text));

        String content = getContent();
        // System.out.println(content);
        assertTrue(content.contains("(To" + start(localId)
                + "piaksen <ref xml:id=\"ref.3\" target=\"note.3\">huo" + end(localId) + "ne"));
        assertTrue(content.contains(
                "Jaana istuu pöyd\u00E4n \u00E4\u00E4ress\u00E4, kutoen sukkaa,"));
    }
    
    @Test
    public void AddNote_end_element_inside_start_element_and_start_element_inside_end_element_()
            throws Exception {
        String startElement = "play-act-stage";
        String endElement = "play-act-stage-ref2";
        String text = "sivulla ra";
        addNote(new SelectedText(startElement, endElement, text));

        String startElement2 = "play-act-stage-ref";
        String endElement2 = "play-act-stage";
        String text2 = "uone\n: per";
        addNote(new SelectedText(startElement2, endElement2, text2), new StringReader(
                target.toString()));
        String content = target.toString();
        // System.out.println(content);
        assertTrue(content.contains("h" + start(localId) + "uone</ref>: per" + end(localId)
                + "\u00E4ll\u00E4"));
    }

    @Test
    public void AddNote_for_p() throws Exception {
        String element = "play-act-sp2-p";
        String text = "sun ullakosta ottaa";
        addNote(new SelectedText(element, element, text));

        String content = getContent();
        assertTrue(content.contains("k\u00E4ski " + start(localId) + text + end(localId)
                + " p\u00E4\u00E4lles"));
    }
    
    @Test
    public void AddNote_for_speaker() throws Exception {
        String element = "play-act-sp-speaker";
        String text = "Esko.";
        addNote(new SelectedText(element, element, text));

        String content = getContent();
        assertTrue(content.contains("<speaker>" + start(localId) + text + end(localId)
                + "</speaker>"));
    }

    @Test
    public void AddNote_huge_difference_between_elements() throws Exception {
        String startElement = "sourceDesc-biblStruct-monogr-imprint-date";
        String endElement = "play-ref";
        String text = "65 [H";
        addNote(new SelectedText(startElement, endElement, text));

        String content = getContent();
//         System.out.println(content);
        assertTrue(content.contains("<date>18" + start(localId) + "65</date>"));
        assertTrue(content.contains("<ref xml:id=\"pageref.1\" target=\"helminauha.xml#pageref.1\">[H" + end(localId) + "elminauha]</ref>"));
    }

    @Test
    public void AddNote_line_breaks_in_selection() throws Exception {
        String startElement = "play-description-castList-castItem8-roleDesc";
        String endElement = "play-description-castList-castItem9-roleDesc";
        String text = " \nori sepp\u00E4\n.\nKarri\n,\ntalon";
        addNote(new SelectedText(startElement, endElement, 1, 1, text));

        String content = getContent();
        assertTrue(content.contains("nu" + start(localId) + "ori"));
        assertTrue(content.contains("talon" + end(localId) + "is\u00E4nt\u00E4"));
    }

    @Test
    public void AddNote_long() throws Exception {
        String element = "play-act-sp-p";
        StringBuilder text = new StringBuilder();
        text.append("matkalle, nimitt\u00E4in h\u00E4\u00E4retkelleni, itsi\u00E4ni sonnustan, ");
        text.append("ja sulhais-vaatteisin puettuna olen, koska h\u00E4n takaisin pal");
        addNote(new SelectedText(element, element, text.toString()));

        String content = getContent();
        // System.out.println(content);
        assertTrue(content.contains(start(localId) + "matkalle, nimitt\u00E4in"));
        assertTrue(content.contains(" takaisin pal" + end(localId)));
    }

    @Test
    public void AddNote_multiple_elements() throws Exception {
        String start = "play-act-sp2-p";
        String end = "play-act-sp3-speaker";
        String text = "ja polvip\u00F6ksyt. Esko.";
        addNote(new SelectedText(start, end, text));

        String content = getContent();
        assertTrue(content.contains(start(localId) + "ja polvip\u00F6ksyt."));
        assertTrue(content.contains("Esko." + end(localId)));
    }

    @Test
    public void AddNote_multiple_elements_2() throws Exception {
        String start = "play-act-sp2-p";
        String end = "play-act-sp3-p";
        String text = "ja polvip\u00F6ksyt. Esko. (panee ty\u00F6ns\u00E4 pois).";
        addNote(new SelectedText(start, end, text));

        String content = getContent();
        assertTrue(content.contains(start(localId) + "ja polvip\u00F6ksyt."));
        assertTrue(content.contains("(panee ty\u00F6ns\u00E4 pois)." + end(localId)));
    }

    @Test
    public void AddNote_on_top_of_another_note_in_child() throws Exception {
        String startElement = "play-act-stage-ref";
        String endElement = "play-act-stage";
        String text = "ne\n: per\u00E4";
        addNote(new SelectedText(startElement, endElement, text));

        String text2 = "uone\n: per\u00E4ll\u00E4 o";
        addNote(new SelectedText(startElement, endElement, 1, 2, text2), new StringReader(
                target.toString()));
        String content = target.toString();
//        System.out.println(content);
        assertTrue(content.contains("<ref xml:id=\"ref.3\" target=\"note.3\">h" + start(localId) + "uo" + start(localId) + "ne</ref>: per\u00E4" + end(localId) + "ll\u00E4 o" + end(localId) + "vi ja akkuna, oikealla"));
    }

    @Test
    public void AddNote_one_char() throws Exception {
        String element = "play-act-stage";
        String text = "i";
        addNote(new SelectedText(element, element, 12, 12, text));

        String content = getContent();
        // System.out.println(content);
        assertTrue(content.contains("v" + start(localId) + "i" + end(localId) + "eress\u00E4,"));
    }

    @Test
    public void AddNote_role_description() throws Exception {
        String startElement = "play-description-castList-castItem7-role";
        String endElement = "play-description-castList-castItem8-roleDesc";
        String text = "\na\n,\nh\u00E4nen tytt\u00E4rens\u00E4, Topiaksen hoitolapsi\n.\n \nKristo\n,\nn";

        addNote(new SelectedText(startElement, endElement, 3, 1, text));

        String newText = "\nna\n,\nh\u00E4nen tytt\u00E4rens\u00E4, Topiaksen hoitolapsi\n.\n \nKristo\n,\nnuori s";
        addNote(new SelectedText(startElement, endElement, 1, 1, newText), new StringReader(target.toString()));

        String content = target.toString();
//        System.out.println(content);
        assertTrue(content.contains("Jaa" + start(localId) + "n" + start(localId) + "a</role>, <roleDesc>h\u00E4nen tytt\u00E4rens\u00E4, Topiaksen\n"));
        assertTrue(content.contains("<castItem><role>Kristo</role>, <roleDesc>n" + end(localId) + "uori s" + end(localId) + "epp\u00E4</roleDesc>.</castItem>"));
    }

    @Test
    public void AddNote_same_element()
            throws Exception {
        String element = "play-act-sp4-p";
        String text = "min\u00E4; ja nytp\u00E4, luulen,";
        addNote(new SelectedText(element, element, text));

        String content = target.toString();
        // System.out.println(content);
        assertTrue(content.contains(start(localId) + "min\u00E4; ja nytp\u00E4, luulen," + end(localId)));
    }

    @Test
    public void AddNote_short_note_1() throws Exception {
        String element = "play-act-stage";
        String text = "es";
        addNote(new SelectedText(element, element, 1, 1, text.toString()));

        String content = getContent();
//         System.out.println(content);
        assertTrue(content.contains("ed" + start(localId) + "es" + end(localId) + "s\u00E4"));
    }

    @Test
    public void AddNote_short_note_2() throws Exception {
        String element = "play-act-stage";
        String text = "es";
        addNote(new SelectedText(element, element, 2, 2, text));

        String content = getContent();
        assertTrue(content.contains("\u00E4\u00E4r" + start(localId) + "es" + end(localId)
                + "s\u00E4,"));
    }

    @Test
    public void AddNote_short_note_3() throws Exception {
        String element = "play-act-stage";
        String text = "es";
        addNote(new SelectedText(element, element, 3, 3, text));

        String content = getContent();
        assertTrue(content.contains("vier" + start(localId) + "es" + end(localId) + "s\u00E4,"));
    }

    @Test
    public void AddNote_start_element_inside_end_element() throws Exception {
        String startElement = "play-act-stage-ref";
        String endElement = "play-act-stage";
        String text = "uone\n: per";
        addNote(new SelectedText(startElement, endElement, text));

        String content = getContent();
        // System.out.println(content);
        assertTrue(content.contains("h" + start(localId) + "uone</ref>: per" + end(localId)
                + "\u00E4ll\u00E4"));
    }

    @Test
    public void AddNote_start_element_inside_end_element_and_end_element_inside_start_element()
            throws Exception {
        String startElement = "play-act-stage-ref";
        String endElement = "play-act-stage";
        String text = "uone\n: per";
        addNote(new SelectedText(startElement, endElement, text));

        String startElement2 = "play-act-stage";
        String endElement2 = "play-act-stage-ref2";
        String text2 = "sivulla ra";
        addNote(new SelectedText(startElement2, endElement2, text2), new StringReader(
                target.toString()));
        String content = target.toString();
        // System.out.println(content);
        assertTrue(content.contains("h" + start(localId) + "uone</ref>: per" + end(localId)
                + "\u00E4ll\u00E4"));
        assertTrue(content.contains("samalla " + start(localId)
                + "sivulla <ref xml:id=\"ref.4\" target=\"note.4\">ra" + end(localId)
                + "hi</ref> ja siin"));
    }

    @Test
    public void AddNote_start_element_inside_end_element_end_does_not_escape() throws Exception {
        String startElement = "play-act-stage-ref";
        String endElement = "play-act-stage";
        String text = "uone\n: p";
        addNote(new SelectedText(startElement, endElement, 1, 2, text));

        String content = getContent();
        // System.out.println(content);
        assertTrue(content.contains("h" + start(localId) + "uone</ref>: p" + end(localId)
                + "er\u00E4ll\u00E4"));
    }

    @Test
    public void AddNote_start_element_inside_end_element2() throws Exception {
        String startElement = "play-act-sp3-p-stage";
        String endElement = "play-act-sp3-p";
        String text = "\u00E4lt\u00E4 ulos) .";
        addNote(new SelectedText(startElement, endElement, 1, 3, text));

        String content = getContent();
//         System.out.println(content);
        assertTrue(content.contains("<stage>(Menee per" + start(localId) + "\u00E4lt\u00E4 ulos)</stage>." + end(localId) + "</p>"));
    }

    @Test
    public void AddNote_twice_overlapping() throws Exception {
        String element = "play-act-sp3-p";
        String text = "\u00E4st";

        addNote(new SelectedText(element, element, text));

        //T-\u00E4st-\u00E4
        String newText = "T\u00E4st\u00E4";
        addNote(new SelectedText(element, element, newText), new StringReader(target.toString()));

        String content = target.toString();
        assertTrue(content.contains(start(localId) + "T" + start(localId) + text + end(localId) + "\u00E4" + end(localId)));
    }

    @Test
    public void AddNote_twice_overlapping2() throws Exception {
        String startElement = "play-description-castList-castItem7-role";
        String endElement = "play-description-castList-castItem8-roleDesc";
        String text = "\na\n,\nh\u00E4nen tytt\u00E4rens\u00E4, Topiaksen hoitolapsi\n.\n \nKristo\n,\nn";

        addNote(new SelectedText(startElement, endElement, 3, 1, text));

        String newText = "\nna\n,\nh\u00E4nen tytt\u00E4rens\u00E4, Topiaksen hoitolapsi\n.\n \nKristo\n,\nnuori s";
        addNote(new SelectedText(startElement, endElement, newText), new StringReader(target.toString()));

        String content = target.toString();
        assertTrue(content.contains("Jaa" + start(localId) + "n" + start(localId) + "a</role>, <roleDesc>h\u00E4nen tytt\u00E4rens\u00E4, Topiaksen\n"));
        assertTrue(content.contains("<castItem><role>Kristo</role>, <roleDesc>n" + end(localId) + "uori s" + end(localId) + "epp\u00E4</roleDesc>.</castItem>"));
    }

    @Test
    public void AddNote_verify_subelement_not_eaten() throws Exception {
        String element = "play-act-stage";
        String text = "Topi";
        addNote(new SelectedText(element, element, text));

        assertTrue(testDocumentContent
                .contains("<ref xml:id=\"ref.4\" target=\"note.4\">rahi</ref>"));
        String content = getContent();
        // System.out.println(content);
        assertTrue(content.contains(start(localId) + text + end(localId)));
        assertTrue(content.contains("<ref xml:id=\"ref.4\" target=\"note.4\">rahi</ref>"));
    }

    private SelectedText createMultipleElementSelectedText(String prevCharacters, String elementCharacters, String characters, String prevContext, String context) {
        int min = generateRandomNumber(0, prevCharacters.length());
        int max = generateRandomNumber(0, characters.length());
        String startSelection = prevCharacters.substring(min);
        String endSelection = characters.substring(0, max);
        String startId = prevContext;
        String endId = context;
        String startWords[] = StringUtils.split(startSelection);
        String endWords[] = StringUtils.split(endSelection);
        if (startWords.length < 1 || endWords.length < 1) {
            return null;
        }
        String firstWord = startWords[0];
        String lastWord = endWords[endWords.length - 1];
        int startIndex = findStartIndex(prevCharacters, firstWord, min);
        int endIndex = findEndIndex(elementCharacters, lastWord, max + elementCharacters.lastIndexOf(characters));
        if (startIndex <= 0 || endIndex <= 0) {
            throw new RuntimeException("Couldn't find occurrences!");
        }

        return new SelectedText(startId, endId, startIndex, endIndex, startSelection + " " + endSelection);
    }

    private List<SelectedText> createSelections() throws Exception {
        List<SelectedText> selections = new ArrayList<SelectedText>();
        Map<String, StringBuilder> contextStrings = new HashMap<String, StringBuilder>();
        ElementContext context = new ElementContext(3);
        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(source);
        String prevCharacters = null;
        String prevContext = null;
        try {
            while (reader.hasNext()) {                
                XMLEvent e = reader.nextEvent();
                if (e.isStartElement()) {
                    context.push(DocumentXMLDaoImpl.extractName(e.asStartElement()));
                } else if (e.isEndElement()) {
                    context.pop();
                } else if (e.isCharacters()) {
                    String characters = WHITESPACE.matcher(e.asCharacters().getData()).replaceAll(" ").trim();
                    if (characters.length() == 0) {
                        continue;
                    }
                    String currentContext = context.getPath();
                    if (contextStrings.containsKey(currentContext)) {
                        contextStrings.get(currentContext).append(" " + characters);
                    } else {
                        contextStrings.put(currentContext, new StringBuilder(characters));
                    }

                    // Generate character block selection
                    SelectedText singleElementSelection = createSingleElementSelectedText(contextStrings.get(currentContext).toString(), characters, currentContext);
                    if (singleElementSelection != null) {
                        selections.add(singleElementSelection);
                    }

                    // Generate character block to next character selection
                    if (prevCharacters == null) {
                        prevCharacters = contextStrings.get(currentContext).toString();
                        prevContext = currentContext;
                        continue;
                    }

                    SelectedText multipleElementSelection = createMultipleElementSelectedText(prevCharacters, contextStrings.get(currentContext).toString(), characters, prevContext, currentContext);
                    if (multipleElementSelection != null) {
                        selections.add(multipleElementSelection);
                    }

                    prevCharacters = contextStrings.get(currentContext).toString();
                    prevContext = currentContext;
                }
            }
        } finally {
            reader.close();
        }
        return selections;
    }

    // TODO Combine logic?
    private SelectedText createSingleElementSelectedText(String elementCharacters, String characters, String context) {
        int min = generateRandomNumber(0, characters.length());
        int max = generateRandomNumber(min, characters.length());
        String selection = characters.substring(min, max);
        String id = context;
        String words[] = StringUtils.split(selection);
        if (words.length < 1) {
            return null;
        }
        String firstWord = words[0];
        String lastWord = words[words.length - 1];
        int startIndex = findStartIndex(elementCharacters, firstWord, min + elementCharacters.indexOf(characters));
        int endIndex = findEndIndex(elementCharacters, lastWord, max + elementCharacters.indexOf(characters));
        if (startIndex <= 0 || endIndex <= 0) {
            throw new RuntimeException("Couldn't find occurrences!");
        }

        return new SelectedText(id, id, startIndex, endIndex, selection);
    }

    private int findEndIndex(String string, String word, int offset) {
        return StringUtils.countMatches(string.substring(0, offset), word);
    }

    private int findStartIndex(String string, String word, int offset) {
        return StringUtils.countMatches(string.substring(0, offset + word.length() + 1 > string.length() ? offset + word.length() : offset + word.length() + 1), word);
    }

    private int generateRandomNumber(int max, int min) {
        return random.nextInt(min - max + 1) + max;
    }

    @Test
    public void Generic_selections_in_cleared_document() throws Exception {        
        List<SelectedText> failedSelectedTexts = new ArrayList<SelectedText>();
        System.err.println("1");
        List<SelectedText> selections = createSelections();
        System.err.println("2");
        for (SelectedText sel : selections) {
            source = new StringReader(testDocumentContent);
            try {
                addNote(sel);
            } catch (NoteAdditionFailedException e) {
                failedSelectedTexts.add(sel);
                System.err.println(sel);
            }
        }
        if (!failedSelectedTexts.isEmpty()) {
            fail("There were " + failedSelectedTexts.size() + " exceptions out of " + selections.size() + ".");
        }
    }

    @Test
    public void Generic_selections_in_unmodified_document() throws Exception {
        List<SelectedText> failedSelectedTexts = new ArrayList<SelectedText>();
        List<SelectedText> selections = createSelections();
        source = new StringReader(testDocumentContent);
        selections.addAll(createSelections());
        source = new StringReader(testDocumentContent);
        selections.addAll(createSelections());
        source = new StringReader(testDocumentContent);
        String content = "";
//        int n = 0;
        for (SelectedText sel : selections) {
            target = new StringWriter();
            try {
                XMLEventReader sourceReader = inFactory.createXMLEventReader(source);
                XMLEventWriter targetWriter = outFactory.createXMLEventWriter(target);
                documentXMLDao.addNote(sourceReader, targetWriter, sel, localId);
                content = target.toString();
            } catch (NoteAdditionFailedException e) {
                failedSelectedTexts.add(sel);
                System.err.println(sel);
            }
            source = new StringReader(content);
//            if (++n == selections.size()) {
//                System.out.println(content);
//            }
        }
        if (!failedSelectedTexts.isEmpty()) {
            fail("There were " + failedSelectedTexts.size() + " exceptions out of " + selections.size() + ".");
        }
    }

    private String getContent() {
        return target.toString();
    }

}
