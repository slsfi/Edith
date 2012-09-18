package com.mysema.edith.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringElementTest {

    @Test
    public void To_String() {
        assertEquals("foo", new StringElement("foo").toString());
    }

    @Test
    public void Copy() {
        StringElement element = new StringElement("foo");
        ParagraphElement copy = element.copy();
        assertTrue(element != copy);
        assertEquals(element.toString(), copy.toString());
    }

}
