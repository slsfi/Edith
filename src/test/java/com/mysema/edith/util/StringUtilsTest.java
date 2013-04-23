package com.mysema.edith.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringUtilsTest {
    
    @Test
    public void Split() {        
        assertNull(StringUtils.split(null));
        assertArrayEquals(new String[0], StringUtils.split(""));
        assertArrayEquals(new String[]{"abc","def"}, StringUtils.split("abc def"));
        assertArrayEquals(new String[]{"abc","def"}, StringUtils.split("abc  def"));
        assertArrayEquals(new String[]{"abc"}, StringUtils.split(" abc "));
    }
    
    @Test
    public void Join() {
        
    }
    
    @Test
    public void isBlank() {
        assertTrue(StringUtils.isBlank(null));
        assertTrue(StringUtils.isBlank(""));
        assertTrue(StringUtils.isBlank(" "));
        assertFalse(StringUtils.isBlank("bob"));
        assertFalse(StringUtils.isBlank("  bob  "));
    }
    
    @Test
    public void CountMatches() {
        assertEquals(0, StringUtils.countMatches(null, "*"));
        assertEquals(0, StringUtils.countMatches("", "*"));
        assertEquals(0, StringUtils.countMatches("abba", null));
        assertEquals(0, StringUtils.countMatches("abba", ""));
        assertEquals(2, StringUtils.countMatches("abba", "a"));
        assertEquals(1, StringUtils.countMatches("abba", "ab"));
        assertEquals(0, StringUtils.countMatches("abba", "xxx"));
        
    }

}
