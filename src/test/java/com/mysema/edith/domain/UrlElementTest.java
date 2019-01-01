/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.domain;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UrlElementTest {
    @Test
    public void To_String() {
        assertEquals("<a>foo</a>", new UrlElement("foo").toString());
    }

    @Test
    public void To_String_With_Reference_Attribute() {
        UrlElement element = new UrlElement("foo");
        element.setUrl("http://www.google.com/");
        assertEquals("<a href=\"http://www.google.com/\">foo</a>", element.toString());
    }

    @Test
    public void Copy() {
        UrlElement element = new UrlElement("foo");
        ParagraphElement copy = element.copy();
        assertTrue(element != copy);
        assertEquals(element.toString(), copy.toString());
    }
}
