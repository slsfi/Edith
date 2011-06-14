package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.EdithTestConstants;
import fi.finlit.edith.dto.SelectedText;
import fi.finlit.edith.ui.services.DocumentRepositoryImpl;

public class NoteAddition2Test extends AbstractServiceTest {
    @Inject
    @Symbol(EdithTestConstants.TEST_DOCUMENT_CONTENT_KEY)
    private String testDocumentContent;

    @Inject
    @Autobuild
    private DocumentRepositoryImpl documentRepo;

    private Reader source;

    private StringWriter target;

    private String localId;

    @Before
    public void setUp() {
        source = new StringReader(testDocumentContent);
        target = new StringWriter();
        localId = UUID.randomUUID().toString();
    }

    private String getContent() {
        return target.toString();
    }

    @After
    public void tearDown() throws Exception {
        source.close();
        target.close();
    }

    private void addNote(SelectedText selectedText, /*InputStream reader*/ Reader reader) throws Exception {
        XMLEventReader sourceReader = inFactory.createXMLEventReader(reader);
        XMLEventWriter targetWriter = outFactory.createXMLEventWriter(target);
        documentRepo.addNote(sourceReader, targetWriter, selectedText, localId);
    }

    private void addNote(SelectedText selectedText) throws Exception {
        addNote(selectedText, source);
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
    public void AddNote_one_char() throws Exception {
        String element = "play-act-stage";
        String text = "i";
        addNote(new SelectedText(element, element, 12, 12, text));

        String content = getContent();
        // System.out.println(content);
        assertTrue(content.contains("v" + start(localId) + "i" + end(localId) + "eress\u00E4,"));
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
    public void AddNote_end_element_inside_start_element() throws Exception {
        String startElement = "play-act-stage";
        String endElement = "play-act-stage-ref";
        String text = "piaksen huo";
        addNote(new SelectedText(startElement, endElement, text));

        String content = getContent();
        // System.out.println(content);
        assertTrue(content.contains("(To" + start(localId)
                + "piaksen <ref xml:id=\"ref.3\" target=\"note.3\">huo" + end(localId) + "ne"));
        assertEquals(1, StringUtils.countMatches(content,
                "Jaana istuu pöyd\u00E4n \u00E4\u00E4ress\u00E4, kutoen sukkaa,"));
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
}
