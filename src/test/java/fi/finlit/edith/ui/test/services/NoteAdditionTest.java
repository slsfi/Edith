/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static fi.finlit.edith.ui.services.DocumentRepositoryImpl.extractName;
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

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.domain.NoteAdditionFailedException;
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

    private StringWriter target;

    private String localId;

    private final Random random = new Random(27);

    private final static Pattern WHITESPACE = Pattern.compile("\\s+");

    @Before
    public void setUp() {
        source = new StringReader(testDocumentContent);
        target = new StringWriter();
        localId = UUID.randomUUID().toString();
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
    public void generic_selections_in_cleared_document() throws Exception {
        List<SelectedText> failedSelectedTexts = new ArrayList<SelectedText>();
        List<SelectedText> selections = createSelections();
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
    public void generic_selections_in_unmodified_document() throws Exception {
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
                documentRepo.addNote(sourceReader, targetWriter, sel, localId);
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
                    context.push(extractName(e.asStartElement()));
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

    private int generateRandomNumber(int max, int min) {
        return random.nextInt(min - max + 1) + max;
    }

    private int findStartIndex(String string, String word, int offset) {
        return StringUtils.countMatches(string.substring(0, offset + word.length() + 1 > string.length() ? offset + word.length() : offset + word.length() + 1), word);
    }

    private int findEndIndex(String string, String word, int offset) {
        return StringUtils.countMatches(string.substring(0, offset), word);
    }
}
