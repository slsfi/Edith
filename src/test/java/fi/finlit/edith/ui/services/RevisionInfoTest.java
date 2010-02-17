package fi.finlit.edith.ui.services;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


public class RevisionInfoTest {
    private RevisionInfo ri;
    private RevisionInfo ri2;

    @Before
    public void setUp() {
        ri = new RevisionInfo(100, "today", "somebody");
        ri2 = new RevisionInfo(100, "yesterday", "somebody else");
    }

    @Test
    public void testEquals() {
        assertTrue(ri.equals(ri2));
    }

    @Test
    public void testHashCode() {
        assertEquals(ri.hashCode(), ri2.hashCode());
    }

    @Test
    public void testRevisionInfo_Long() {
        RevisionInfo revisionInfo = new RevisionInfo(666);
        assertEquals("", revisionInfo.getCreated());
        assertEquals("", revisionInfo.getCreator());
    }
}
