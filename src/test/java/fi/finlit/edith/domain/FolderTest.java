package fi.finlit.edith.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * FolderTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class FolderTest {
    
    private Folder folder1 = new Folder();
    
    @Before
    public void setUp(){
        folder1.setSvnPath("path");
    }

    @Test
    public void testHashCode() {
        assertEquals("path".hashCode(), folder1.hashCode());
    }

    @Test
    public void testCompareTo() {
        Folder folder2 = new Folder();
        folder2.setSvnPath("z");
        assertTrue(folder1.compareTo(folder2) < 0);
    }

    @Test
    public void testEqualsObject() {
        Folder folder2 = new Folder();
        folder2.setSvnPath("z");
        assertFalse(folder1.equals(folder2));
    }

}
