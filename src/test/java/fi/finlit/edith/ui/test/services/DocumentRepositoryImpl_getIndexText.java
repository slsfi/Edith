package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.*;

import org.junit.Test;

import fi.finlit.edith.ui.services.DocumentRepositoryImpl;

/**
 * IndexOfText provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRepositoryImpl_getIndexText {
    
    @Test
    public void test(){
        assertEquals(8, DocumentRepositoryImpl.getIndex("This is a sentence", "a", 1));
        assertEquals(5, DocumentRepositoryImpl.getIndex("This is a sentence", "is", 2));
        assertEquals(-1, DocumentRepositoryImpl.getIndex("This is a sentence", "b", 1));
        
        assertEquals(5, DocumentRepositoryImpl.getIndex("This is a is ", "is", 2));
        assertEquals(10, DocumentRepositoryImpl.getIndex("This is a is ", "is", 3));
        
        assertEquals(0, DocumentRepositoryImpl.getIndex("This is a", "This", 1));
        assertEquals(8, DocumentRepositoryImpl.getIndex("This is a", "a", 1));
        assertEquals(3, DocumentRepositoryImpl.getIndex("12 123", "123", 1));
    }

}
