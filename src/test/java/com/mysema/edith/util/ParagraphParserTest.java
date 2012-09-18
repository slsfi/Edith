package com.mysema.edith.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ParagraphParserTest {

    @Test
    public void Parse_Returns_Null_If_Input_Is_Null() {
        assertNull(ParagraphParser.parseSafe(null));
    }

    @Test
    public void Parsed_Plain_String_Has_One_String_Element() {
        assertEquals(1, ParagraphParser.parseSafe("foo").getElements().size());
    }

}
