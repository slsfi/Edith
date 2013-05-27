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

    private static final String PREFIX = "TEI-text0-body0-";
    
    private static final String HEADER_PREFIX = "TEI-teiHeader0-fileDesc0-";
    
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

    private int addNote(SelectedText selectedText) throws Exception {
        return addNote(selectedText, source);
    }

    private int addNote(SelectedText selectedText, /*InputStream reader*/ Reader reader) throws Exception {
        target = new StringWriter();
        XMLEventReader sourceReader = inFactory.createXMLEventReader(reader);
        XMLEventWriter targetWriter = outFactory.createXMLEventWriter(target);
        return documentXMLDao.addNote(sourceReader, targetWriter, selectedText, localId);
    }

    @Before
    public void setUp() {
        source = new StringReader(testDocumentContent);               
        localId = (long) UUID.randomUUID().hashCode();
    }

    @After
    public void tearDown() throws Exception {
        source.close();
        target.close();
    }
    
    @Test
    public void AddNote_header() throws Exception {        
        String startNode = "TEI-teiHeader0-fileDesc0-sourceDesc0-biblStruct0-monogr0-author0";
        String endNode = "TEI-teiHeader0-fileDesc0-sourceDesc0-biblStruct0-monogr0-author0";
        String selection = "ivi, Aleksi";
        int startCharIndex = 0;
        int endCharIndex = 2;
        
        addNote(new SelectedText(startNode, endNode, startCharIndex, endCharIndex, selection));
        
        String content = getContent();
        assertTrue(content.contains(start(localId)+"ivi, Aleksi"+end(localId)));
        
    }
    
    @Test
    public void AddNote_end_element_deeply_inside_start_element() throws Exception {
        String startElement = "div0-div0-castList0-castItem12";
        String endElement = "div0-div0-castList0-castItem12-roleDesc0-ref0";
        String text = ", kraatari";
        addNote(new SelectedText(PREFIX + startElement, PREFIX + endElement, text));

        String content = getContent();
//         System.out.println(content);
        assertTrue(content.contains("<castItem><role>Antres</role>" + start(localId) + ", <roleDesc><ref xml:id=\"ref.1\" target=\"note.1\">kraatari" + end(localId) + "</ref> ja"));
    }

    @Test
    public void AddNote_end_element_inside_start_element() throws Exception {
        String startElement = "div0-div1-stage0";
        String endElement = "div0-div1-stage0-ref0";
        String text = "piaksen huo";
        addNote(new SelectedText(PREFIX + startElement, PREFIX + endElement, text));

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
        String startElement = "div0-div1-stage0";
        String endElement = "div0-div1-stage0-ref1";
        String text = "sivulla ra";
        addNote(new SelectedText(PREFIX + startElement, PREFIX + endElement, text));

        String startElement2 = "div0-div1-stage0-ref0";
        String endElement2 = "div0-div1-stage0";
        String text2 = "uone\n: per";
        addNote(new SelectedText(PREFIX + startElement2, PREFIX + endElement2, text2), new StringReader(
                target.toString()));
        String content = target.toString();
        // System.out.println(content);
        assertTrue(content.contains("h" + start(localId) + "uone</ref>: per" + end(localId)
                + "\u00E4ll\u00E4"));
    }

    @Test
    public void AddNote_for_p() throws Exception {
        String element = "div0-div1-sp1-p0";
        String text = "sun ullakosta ottaa";
        addNote(new SelectedText(PREFIX + element, PREFIX + element, 1, 4, text));

        String content = getContent();        
        assertTrue(content.contains("k\u00E4ski " + start(localId) + text + end(localId)
                + " p\u00E4\u00E4lles"));
    }
    
    @Test
    public void AddNote_for_speaker() throws Exception {
        String element = "div0-div1-sp0-speaker0";
        String text = "Esko.";
        addNote(new SelectedText(PREFIX + element, PREFIX + element, text));

        String content = getContent();
        assertTrue(content.contains("<speaker>" + start(localId) + text + end(localId)
                + "</speaker>"));
    }

    @Test
    public void AddNote_huge_difference_between_elements() throws Exception {
        String startElement = "sourceDesc0-biblStruct0-monogr0-imprint0-date0";
        String endElement = "div0-ref0";
        String text = "65 [H";
        addNote(new SelectedText(HEADER_PREFIX + startElement, PREFIX + endElement, text));

        String content = getContent();
//         System.out.println(content);
        assertTrue(content.contains("<date>18" + start(localId) + "65</date>"));
        assertTrue(content.contains("<ref xml:id=\"pageref.1\" target=\"helminauha.xml#pageref.1\">[H" + end(localId) + "elminauha]</ref>"));
    }

    @Test
    public void AddNote_line_breaks_in_selection() throws Exception {
        String startElement = "div0-div0-castList0-castItem7-roleDesc0";
        String endElement = "div0-div0-castList0-castItem8-roleDesc0";
        String text = "ori sepp\u00E4\n.\nKarri\n,\ntalon";
        addNote(new SelectedText(PREFIX + startElement, PREFIX + endElement, 0, 0, text));

        String content = getContent();
//        System.err.println(content);
        assertTrue(content.contains("nu" + start(localId) + "ori"));
        assertTrue(content.contains("talon" + end(localId) + "is\u00E4nt\u00E4"));
    }

    @Test
    public void AddNote_long() throws Exception {
        String element = "div0-div1-sp0-p0";
        StringBuilder text = new StringBuilder();
        text.append("matkalle, nimitt\u00E4in h\u00E4\u00E4retkelleni, itsi\u00E4ni sonnustan, ");
        text.append("ja sulhais-vaatteisin puettuna olen, koska h\u00E4n takaisin pal");
        addNote(new SelectedText(PREFIX + element, PREFIX + element, 0, 8, text.toString()));

        String content = getContent();
//        System.err.println(content);
        assertTrue(content.contains(start(localId) + "matkalle, nimitt\u00E4in"));
        assertTrue(content.contains(" takaisin pal" + end(localId)));
    }

    @Test
    public void AddNote_multiple_elements() throws Exception {
        String start = "div0-div1-sp1-p0";
        String end = "div0-div1-sp2-speaker0";
        String text = "ja polvip\u00F6ksyt. Esko.";
        addNote(new SelectedText(PREFIX + start, PREFIX + end, text));

        String content = getContent();
        assertTrue(content.contains(start(localId) + "ja polvip\u00F6ksyt."));
        assertTrue(content.contains("Esko." + end(localId)));
    }

    @Test
    public void AddNote_multiple_elements_2() throws Exception {
        String start = "div0-div1-sp1-p0";
        String end = "div0-div1-sp2-p0";
        String text = "ja polvip\u00F6ksyt. Esko. (panee ty\u00F6ns\u00E4 pois).";
        addNote(new SelectedText(PREFIX + start, PREFIX + end, text));

        String content = getContent();
        assertTrue(content.contains(start(localId) + "ja polvip\u00F6ksyt."));
        assertTrue(content.contains("(panee ty\u00F6ns\u00E4 pois)." + end(localId)));
    }

    @Test
    public void AddNote_on_top_of_another_note_in_child() throws Exception {
        String startElement = "div0-div1-stage0-ref0";
        String endElement = "div0-div1-stage0";
        String text = "ne\n: per\u00E4";
        addNote(new SelectedText(PREFIX + startElement, PREFIX + endElement, text));

        String text2 = "uone\n: per\u00E4ll\u00E4 o";
        addNote(new SelectedText(PREFIX + startElement, PREFIX + endElement, 0, 1, text2), 
                new StringReader(target.toString()));
        String content = target.toString();
//        System.out.println(content);
        assertTrue(content.contains("<ref xml:id=\"ref.3\" target=\"note.3\">h" + start(localId) + "uo" + start(localId) + "ne</ref>: per\u00E4" + end(localId) + "ll\u00E4 o" + end(localId) + "vi ja akkuna, oikealla"));
    }

    @Test
    public void AddNote_one_char() throws Exception {
        String element = "div0-div1-stage0";
        String text = "i";
        addNote(new SelectedText(PREFIX + element, PREFIX + element, 11, 11, text));

        String content = getContent();
        // System.out.println(content);
        assertTrue(content.contains("v" + start(localId) + "i" + end(localId) + "eress\u00E4,"));
    }

    @Test
    public void AddNote_role_description() throws Exception {
        String startElement = "div0-div0-castList0-castItem6-role0";
        String endElement = "div0-div0-castList0-castItem7-roleDesc0";
        String text = "a\n,\nh\u00E4nen tytt\u00E4rens\u00E4, Topiaksen hoitolapsi\n.\n \nKristo\n,\nn";

        addNote(new SelectedText(PREFIX + startElement, PREFIX + endElement, 2, 0, text));
        
        String newText = "na\n,\nh\u00E4nen tytt\u00E4rens\u00E4, Topiaksen hoitolapsi\n.\n \nKristo\n,\nnuori s";
        addNote(new SelectedText(PREFIX + startElement, PREFIX + endElement, 0, 0, newText), 
                new StringReader(target.toString()));

        String content = target.toString();
        //System.out.println(content.substring(0, 8000));
        assertTrue(content.contains("Jaa" + start(localId) + "n" + start(localId) + "a</role>, <roleDesc>h\u00E4nen tytt\u00E4rens\u00E4, Topiaksen\n"));
        assertTrue(content.contains("<castItem><role>Kristo</role>, <roleDesc>n" + end(localId) + "uori s" + end(localId) + "epp\u00E4</roleDesc>.</castItem>"));
    }

    @Test
    public void AddNote_same_element()
            throws Exception {
        String element = "div0-div1-sp3-p0";
        String text = "min\u00E4; ja nytp\u00E4, luulen,";
        addNote(new SelectedText(PREFIX + element, PREFIX + element, 6, 4, text));

        String content = target.toString();
        //System.out.println(content);
        assertTrue(content.contains(start(localId) + "min\u00E4; ja nytp\u00E4, luulen," + end(localId)));
    }

    @Test
    public void AddNote_short_note_1() throws Exception {
        String element = "div0-div1-stage0";
        String text = "es";
        addNote(new SelectedText(PREFIX + element, PREFIX + element, 7, 3, text));

        String content = getContent();
//        System.out.println(content);
        assertTrue(content.contains("ed" + start(localId) + "es" + end(localId) + "s\u00E4"));
    }

    @Test
    public void AddNote_short_note_2() throws Exception {
        String element = "div0-div1-stage0";
        String text = "es";
        addNote(new SelectedText(PREFIX + element, PREFIX + element, 8, 10, text));

        String content = getContent();
//        System.out.println(content);
        assertTrue(content.contains("\u00E4\u00E4r" + start(localId) + "es" + end(localId)
                + "s\u00E4,"));
    }

    @Test
    public void AddNote_short_note_3() throws Exception {
        String element = "div0-div1-stage0";
        String text = "es";
        addNote(new SelectedText(PREFIX + element, PREFIX + element, 11, 14, text));

        String content = getContent();
//        System.out.println(content);
        assertTrue(content.contains("vier" + start(localId) + "es" + end(localId) + "s\u00E4,"));
    }

    @Test
    public void AddNote_start_element_inside_end_element() throws Exception {
        String startElement = "div0-div1-stage0-ref0";
        String endElement = "div0-div1-stage0";
        String text = "uone\n: per";
        addNote(new SelectedText(PREFIX + startElement, PREFIX + endElement, text));

        String content = getContent();
        // System.out.println(content);
        assertTrue(content.contains("h" + start(localId) + "uone</ref>: per" + end(localId)
                + "\u00E4ll\u00E4"));
    }

    @Test
    public void AddNote_start_element_inside_end_element_and_end_element_inside_start_element()
            throws Exception {
        String startElement = "div0-div1-stage0-ref0";
        String endElement = "div0-div1-stage0";
        String text = "uone\n: per";
        addNote(new SelectedText(PREFIX + startElement, PREFIX + endElement, text));

        String startElement2 = "div0-div1-stage0";
        String endElement2 = "div0-div1-stage0-ref1";
        String text2 = "sivulla ra";
        addNote(new SelectedText(PREFIX + startElement2, PREFIX + endElement2, 6, 0, text2), 
                new StringReader(target.toString()));
        String content = target.toString();
        //System.out.println(content);
        assertTrue(content.contains("h" + start(localId) + "uone</ref>: per" + end(localId)
                + "\u00E4ll\u00E4"));
        assertTrue(content.contains("samalla " + start(localId)
                + "sivulla <ref xml:id=\"ref.4\" target=\"note.4\">ra" + end(localId)
                + "hi</ref> ja siin"));
    }

    @Test
    public void AddNote_start_element_inside_end_element_end_does_not_escape() throws Exception {
        String startElement = "div0-div1-stage0-ref0";
        String endElement = "div0-div1-stage0";
        String text = "uone\n: p";
        addNote(new SelectedText(PREFIX + startElement, PREFIX + endElement, 0, 1, text));

        String content = getContent();
        // System.out.println(content);
        assertTrue(content.contains("h" + start(localId) + "uone</ref>: p" + end(localId)
                + "er\u00E4ll\u00E4"));
    }

    @Test
    public void AddNote_start_element_inside_end_element2() throws Exception {
        String startElement = "div0-div1-sp2-p0-stage0";
        String endElement = "div0-div1-sp2-p0";
        String text = "\u00E4lt\u00E4 ulos) .";
        addNote(new SelectedText(PREFIX + startElement, PREFIX + endElement, 0, 2, text));

        String content = getContent();
//         System.out.println(content);
        assertTrue(content.contains("<stage>(Menee per" + start(localId) + "\u00E4lt\u00E4 ulos)</stage>." + end(localId) + "</p>"));
    }

    @Test
    public void AddNote_twice_overlapping() throws Exception {
        String element = "div0-div1-sp2-p0";
        String text = "\u00E4st";

        addNote(new SelectedText(PREFIX + element, PREFIX + element, 1, 1, text));

        //T-\u00E4st-\u00E4
        String newText = "T\u00E4st\u00E4";
        addNote(new SelectedText(PREFIX + element, PREFIX + element, 0, 2, newText), 
                new StringReader(target.toString()));

        String content = target.toString();
//        System.err.println(content.substring(0, 8000));
        assertTrue(content.contains(start(localId) + "T" + start(localId) + text + end(localId) + "\u00E4" + end(localId)));
    }

    @Test
    public void AddNote_twice_overlapping2() throws Exception {
        String startElement = "div0-div0-castList0-castItem6-role0";
        String endElement = "div0-div0-castList0-castItem7-roleDesc0";
        String text = "a\n,\nh\u00E4nen tytt\u00E4rens\u00E4, Topiaksen hoitolapsi\n.\n \nKristo\n,\nn";

        addNote(new SelectedText(PREFIX + startElement, PREFIX + endElement, 2, 0, text));

        String newText = "na\n,\nh\u00E4nen tytt\u00E4rens\u00E4, Topiaksen hoitolapsi\n.\n \nKristo\n,\nnuori s";
        addNote(new SelectedText(PREFIX + startElement, PREFIX + endElement, newText), new StringReader(target.toString()));

        String content = target.toString();
        assertTrue(content.contains("Jaa" + start(localId) + "n" + start(localId) + "a</role>, <roleDesc>h\u00E4nen tytt\u00E4rens\u00E4, Topiaksen\n"));
        assertTrue(content.contains("<castItem><role>Kristo</role>, <roleDesc>n" + end(localId) + "uori s" + end(localId) + "epp\u00E4</roleDesc>.</castItem>"));
    }

    @Test
    public void AddNote_verify_subelement_not_eaten() throws Exception {
        String element = "div0-div1-stage0";
        String text = "Topi";
        addNote(new SelectedText(PREFIX + element, PREFIX + element, text));

        assertTrue(testDocumentContent
                .contains("<ref xml:id=\"ref.4\" target=\"note.4\">rahi</ref>"));
        String content = getContent();
        // System.out.println(content);
        assertTrue(content.contains(start(localId) + text + end(localId)));
        assertTrue(content.contains("<ref xml:id=\"ref.4\" target=\"note.4\">rahi</ref>"));
    }
    
    @Test
    public void AddNote_with_comment() throws Exception {
        String xml = "<a><b><c><p rend='normalizedIndent'>" +
            "Så mycket nu om flickorna, sedan wi denna gången förbigå det vexande slägtet. (Bland fruarna dansa:" + 
            "<persName rend='underline'>Johanna Frosterus</persName>, den bästa valseuse<!--foreign?--> i"  +
            "<placeName>NyCarleby</placeName>, <persName rend='underline'>Fru Synnerberg</persName>, fruarne" + 
            "<persName rend='underline'>Turdin</persName>, <persName rend='underline'>Hammarin</persName>," + 
            "<persName rend='underline'>Lithén</persName> och <persName rend='underline'>Benzelstjerna</persName>.)" +
            "</p></c></b></a>"; 
        
        String start = "a-b0-c0-p0-persName0";
        String end = start;        
        addNote(new SelectedText(start, end, "Johanna"), new StringReader(xml));  
        
        String content = target.toString();
//        System.out.println(content);
        assertTrue(content.contains("valseuse<!--foreign?--> i"));
    } 
    
    @Test
    public void AddNote_with_quotes() throws Exception {
        String xml = "<a><b><c><p rend='normalizedIndent'>" +
                "Så mycket nu om flickorna, \"sedan wi denna gången förbigå det vexande slägtet\". (Bland fruarna dansa:" + 
                "<persName rend='underline'>Johanna Frosterus</persName>, den bästa valseuse<!--foreign?--> i"  +
                "<placeName>NyCarleby</placeName>, <persName rend='underline'>Fru Synnerberg</persName>, fruarne" + 
                "<persName rend='underline'>Turdin</persName>, <persName rend='underline'>Hammarin</persName>," + 
                "<persName rend='underline'>Lithén</persName> och <persName rend='underline'>Benzelstjerna</persName>.)" +
                "</p></c></b></a>"; 
            
        String start = "a-b0-c0-p0";
        String end = start;        
        addNote(new SelectedText(start, end, 0, 1, "\"sedan wi denna gången förbigå det vexande slägtet\""), new StringReader(xml));  
            
        String content = target.toString();
        System.out.println(content);
        assertTrue(content.contains(
                "/>\"sedan wi denna gången förbigå det vexande slägtet\"<"));
    } 
    
    @Test
    public void Add_Note_For_Split_Word_Deeply_Nested() throws Exception {
        String start = "div0-div1-sp1-p0-place0-subst0-add0";
        String end = "div0-div1-sp1-p0-place0";
        String text = "M\nazara";
        addNote(new SelectedText(PREFIX + start, PREFIX + end, 0, 2, text));

        String content = getContent();
        assertTrue(content.contains("<place><subst><del>m</del><add>" +
                                    start(localId) +
                                    "M</add></subst>azara" +
                                    end(localId) + "</place> win</p>"));
    }

    @Test
    public void Add_Note_For_Split_Word_Two_Words_Deeply_Nested() throws Exception {
        String start = "div0-div1-sp1-p0-place0-subst0-add0";
        String end = "div0-div1-sp1-p0";
        String text = "M\nazara win";
        addNote(new SelectedText(PREFIX + start, PREFIX + end, 0, 8, text));

        String content = getContent();
        System.err.println(content);
        assertTrue(content.contains("<subst><del>m</del><add>" +
                                    start(localId) +
                                    "M</add></subst>azara</place> win" +
                                    end(localId) + "</p>"));
    } 
    
    @Test
    public void Position_In_Order() throws Exception {
        // play-act-sp-p[1] , play-act-sp-p[1] : Jaana
        int pos1 = addNote(new SelectedText(PREFIX + "div0-div1-sp0-p0", PREFIX + "div0-div1-sp0-p0", 0, 2, "Jaana"));        
        // play-act-sp-p[1] , play-act-sp-p[1] : kylään
        int pos2 = addNote(new SelectedText(PREFIX + "div0-div1-sp0-p0", PREFIX + "div0-div1-sp0-p0", 0, 3, "kylään"), new StringReader(target.toString()));        
        // play-act-sp2-p[1] , play-act-sp2-p[1] : käski
        int pos3 = addNote(new SelectedText(PREFIX + "div0-div1-sp1-p0", PREFIX + "div0-div1-sp1-p0", 0, 0, "käski"), new StringReader(target.toString()));        
        // play-act-sp-p[1] , play-act-sp-p[1] : äitini
        int pos4 = addNote(new SelectedText(PREFIX + "div0-div1-sp0-p0", PREFIX + "div0-div1-sp0-p0", 0, 0, "äitini"), new StringReader(target.toString()));
        // play-act-sp2-p[1] , play-act-sp2-p[1] : päälles
        int pos5 = addNote(new SelectedText(PREFIX + "div0-div1-sp1-p0", PREFIX + "div0-div1-sp1-p0", 0, 3, "päälles"), new StringReader(target.toString()));
        // play-act-sp2-p[1] , play-act-sp2-p[1] : ullakosta
        // 3359 -> 3372
        int pos6 = addNote(new SelectedText(PREFIX + "div0-div1-sp1-p0", PREFIX + "div0-div1-sp1-p0", 1, 1, "ullakosta"), new StringReader(target.toString()));
        // play-act-sp-p[1] , play-act-sp-p[1] : takaisin
        // 3037 -> 3244
        int pos7 = addNote(new SelectedText(PREFIX + "div0-div1-sp0-p0", PREFIX + "div0-div1-sp0-p0", 14, 16, "takaisin"), new StringReader(target.toString())); // FIXME
        
        // Jaana, äitini, takaisin, ullakosta, päälles
        assertTrue(pos4 > pos1);
        assertTrue(pos7 > pos4);
        assertTrue(pos6 > pos7);
        assertTrue(pos5 > pos6);
        
        System.out.println(pos1);
        System.out.println(pos2);
        System.out.println(pos3);
        System.out.println(pos4);
        System.out.println(pos5);
        System.out.println(pos6);
        System.out.println(pos7);
    } 

    private SelectedText createMultipleElementSelectedText(String prevCharacters, String elementCharacters, String characters, String prevContext, String context) {
        int min = generateRandomNumber(0, prevCharacters.length());
        int max = generateRandomNumber(1, characters.length());
        String startSelection = prevCharacters.substring(min).trim(); // XXX no ws at start or end
        String endSelection = characters.substring(0, max).trim(); // XXX no ws at start or end
        String startId = prevContext;
        String endId = context;

        char firstChar = startSelection.charAt(0);
        char lastChar = endSelection.charAt(endSelection.length() - 1);
        int startIndex = findStartIndex(prevCharacters, firstChar, min);
        int endIndex = findEndIndex(elementCharacters, lastChar, max + elementCharacters.lastIndexOf(characters));
        if (startIndex < 0 || endIndex < 0) {
            throw new RuntimeException("Couldn't find occurrences!");
        }

        return new SelectedText(startId, endId, startIndex, endIndex, startSelection + " " + endSelection);
    }

    private List<SelectedText> createSelections() throws Exception {
        List<SelectedText> selections = new ArrayList<SelectedText>();
        Map<String, StringBuilder> contextStrings = new HashMap<String, StringBuilder>();
        ElementContext context = new ElementContext();
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
                    SelectedText singleElementSelection = createSingleElementSelectedText(
                            contextStrings.get(currentContext).toString(), characters, currentContext);
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
        if (characters.isEmpty()) {
            return null;
        }
        
        int min = generateRandomNumber(0, characters.length() - 1);
        int max = generateRandomNumber(min + 1, characters.length());
        String selection = characters.substring(min, max).trim(); // XXX no whitespace at start or end        
        if (selection.isEmpty()) {
            return null;
        }
        
        String id = context;
        char firstChar = selection.charAt(0);
        char lastChar = selection.charAt(selection.length() - 1);
        int startIndex = findStartIndex(elementCharacters, firstChar, min + elementCharacters.indexOf(characters));
        int endIndex = findEndIndex(elementCharacters, lastChar, max + elementCharacters.indexOf(characters));
        if (startIndex < 0 || endIndex < 0) {
            throw new RuntimeException("Couldn't find occurrences!");
        }

        return new SelectedText(id, id, startIndex, endIndex, selection);
    }

    private int findEndIndex(String string, char ch, int offset) {
        return StringUtils.countMatches(string.substring(0, offset), ch) - 1;
    }

    private int findStartIndex(String string, char ch, int offset) {
        return StringUtils.countMatches(
                string.substring(0, offset + 2 > string.length()  ? offset + 1 : offset + 2), ch) - 1;
    }

    private int generateRandomNumber(int min, int max) {
        if (max > min) {
            return min + random.nextInt(max - min);    
        } else {
            return min;
        }        
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
            for (SelectedText sel : failedSelectedTexts) {
                System.err.println(sel);
            }            
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
            for (SelectedText sel : failedSelectedTexts) {
                System.err.println(sel);
            }   
            fail("There were " + failedSelectedTexts.size() + " exceptions out of " + selections.size() + ".");
        }
    }

    private String getContent() {
        return target.toString();
    }

}
