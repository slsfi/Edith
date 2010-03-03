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
    public void generic_selections() throws Exception {
        for (SelectedText sel : createSelections()) {
            System.out.println(sel);
            addNote(sel);
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
