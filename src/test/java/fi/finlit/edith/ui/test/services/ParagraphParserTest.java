package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.domain.Paragraph;
import fi.finlit.edith.domain.ParagraphElement;
import fi.finlit.edith.domain.StringElement;
import fi.finlit.edith.ui.services.ParagraphParser;

public class ParagraphParserTest {
    private Paragraph paragraph;

    @Test
    public void Parse_Paragraph_Containing_Bibliograph() throws XMLStreamException {
        paragraph = ParagraphParser.parseParagraph("<bibliograph>foo</bibliograph>");
        assertEquals("<bibliograph>foo</bibliograph>", paragraph.toString());
    }

    @Test
    public void Parse_Paragraph_Containing_Bibliograph_And_Text_Elements()
            throws XMLStreamException {
        paragraph = ParagraphParser.parseParagraph("bar <bibliograph>foo</bibliograph> bar");
        assertEquals("bar <bibliograph>foo</bibliograph> bar", paragraph.toString());
    }

    @Test
    public void Parse_Paragraph_Containing_Bibliograph_With_A_Reference() throws XMLStreamException {
        paragraph = ParagraphParser.parseParagraph("<bibliograph ref=\"boo\">foo</bibliograph>");
        assertEquals("<bibliograph ref=\"boo\">foo</bibliograph>", paragraph.toString());
    }

    @Test
    public void Parse_Paragraph_Containing_Bibliographs_And_Text_Elements()
            throws XMLStreamException {
        paragraph = ParagraphParser
                .parseParagraph("bar <bibliograph>foo</bibliograph> bar <bibliograph>foobar</bibliograph>");
        assertEquals("bar <bibliograph>foo</bibliograph> bar <bibliograph>foobar</bibliograph>",
                paragraph.toString());
    }

    @Test
    public void Parse_Paragraph_From_Multiple_Words() throws XMLStreamException {
        paragraph = ParagraphParser.parseParagraph("foo bar");
        assertEquals("foo bar", paragraph.toString());
    }

    @Test
    public void Parse_Paragraph_From_Single_Word() throws XMLStreamException {
        paragraph = ParagraphParser.parseParagraph("foo");
        assertEquals("foo", paragraph.toString());
    }

    @Before
    public void setUp() throws Exception {
        paragraph = new Paragraph();
    }

    @Test
    public void testAddElement() {
        paragraph.addElement(new StringElement("el"));
        assertEquals("el", paragraph.toString());
    }

}
