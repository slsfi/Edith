package fi.finlit.edith.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class DocumentTest {

    private final Document doc1 = new Document();

    @Before
    public void setUp(){
        doc1.setSvnPath("path");
    }

    @Test
    public void testHashCode() {
       assertEquals("path".hashCode(), doc1.hashCode());
    }

    @Test
    public void testCompareTo() {
        Document doc2 = new Document();
        doc2.setSvnPath("z");
        assertTrue(doc1.compareTo(doc2) < 0);
    }

    @Test
    public void testEqualsObject() {
        Document doc2 = new Document();
        doc2.setSvnPath("z");
        assertFalse(doc1.equals(doc2));
    }

}
