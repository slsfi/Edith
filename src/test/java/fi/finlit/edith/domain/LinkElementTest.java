package fi.finlit.edith.domain;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LinkElementTest {

    @Test
    public void To_String() {
        assertEquals("<bibliograph>foo</bibliograph>", new LinkElement("foo").toString());
    }

    @Test
    public void To_String_With_Reference_Attribute() {
        LinkElement element = new LinkElement("foo");
        element.setReference("bar");
        assertEquals("<bibliograph ref=\"bar\">foo</bibliograph>", element.toString());
    }

    @Test
    public void Copy() {
        LinkElement element = new LinkElement("foo");
        ParagraphElement copy = element.copy();
        assertTrue(element != copy);
        assertEquals(element.toString(), copy.toString());
    }
}
