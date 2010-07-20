package fi.finlit.edith.domain;


import static org.junit.Assert.assertEquals;

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
}
