package fi.finlit.edith.domain;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Test;

public class ParagraphTest {
    private Paragraph paragraph;

    @Before
    public void setUp() throws Exception {
        paragraph = new Paragraph(new ArrayList<ParagraphElement>());
    }

    @Test
    public void testAddElement() {
        paragraph.addElement(new StringElement("el"));
        assertEquals("el", paragraph.toString());
    }

    @Test
    public void Parse_Paragraph_From_Single_Word() throws XMLStreamException  {
        paragraph = Paragraph.parseParagraph("foo");
        assertEquals("foo", paragraph.toString());
    }

    @Test
    public void Parse_Paragraph_From_Multiple_Words() throws XMLStreamException  {
        paragraph = Paragraph.parseParagraph("foo bar");
        assertEquals("foo bar", paragraph.toString());
    }

    @Test
    public void Parse_Paragraph_Containing_Bibliograph() throws XMLStreamException  {
        paragraph = Paragraph.parseParagraph("<bibliograph>foo</bibliograph>");
        assertEquals("<bibliograph>foo</bibliograph>", paragraph.toString());
    }

    @Test
    public void Parse_Paragraph_Containing_Bibliograph_With_A_Reference() throws XMLStreamException  {
        paragraph = Paragraph.parseParagraph("<bibliograph ref=\"boo\">foo</bibliograph>");
        assertEquals("<bibliograph ref=\"boo\">foo</bibliograph>", paragraph.toString());
    }

    @Test
    public void Parse_Paragraph_Containing_Bibliograph_And_Text_Elements() throws XMLStreamException  {
        paragraph = Paragraph.parseParagraph("bar <bibliograph>foo</bibliograph> bar");
        assertEquals("bar <bibliograph>foo</bibliograph> bar", paragraph.toString());
    }

    @Test
    public void Parse_Paragraph_Containing_Bibliographs_And_Text_Elements() throws XMLStreamException  {
        paragraph = Paragraph.parseParagraph("bar <bibliograph>foo</bibliograph> bar <bibliograph>foobar</bibliograph>");
        assertEquals("bar <bibliograph>foo</bibliograph> bar <bibliograph>foobar</bibliograph>", paragraph.toString());
    }

}
