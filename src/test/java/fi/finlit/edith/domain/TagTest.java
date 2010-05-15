package fi.finlit.edith.domain;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * TagTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class TagTest {

    private Tag tag1 = new Tag("x"), tag2 = new Tag("y");
    
    @Test
    public void testHashCode() {
        assertEquals("x".hashCode(), tag1.hashCode());
    }

    @Test
    public void testCompareTo() {
        assertTrue(tag1.compareTo(tag2) < 0);
    }

    @Test
    public void testEqualsObject() {
        assertFalse(tag1.equals(tag2));
    }

}
