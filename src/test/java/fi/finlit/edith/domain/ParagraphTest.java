package fi.finlit.edith.domain;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

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

}
