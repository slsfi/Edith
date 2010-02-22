/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
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
    public void shortText(){
        assertEquals(8, DocumentRepositoryImpl.getIndex("This is a sentence", "a", 1));
        assertEquals(5, DocumentRepositoryImpl.getIndex("This is a sentence", "is", 2));
        assertEquals(-1, DocumentRepositoryImpl.getIndex("This is a sentence", "b", 1));
        
        assertEquals(5, DocumentRepositoryImpl.getIndex("This is a is ", "is", 2));
        assertEquals(10, DocumentRepositoryImpl.getIndex("This is a is ", "is", 3));
        
        assertEquals(0, DocumentRepositoryImpl.getIndex("This is a", "This", 1));
        assertEquals(8, DocumentRepositoryImpl.getIndex("This is a", "a", 1));
        assertEquals(3, DocumentRepositoryImpl.getIndex("12 123", "123", 1));
    }
    
    @Test
    public void longText1(){
        StringBuilder builder = new StringBuilder();
        builder.append("(Topiaksen huone: perällä ovi ja akkuna, oikealla pöytä, ");
        builder.append("vasemmalla sivu-ovi ja enemmän edessä samalla sivulla rahi ja siinä suutarin ");
        builder.append("kaluja. Jaana istuu pöydän ääressä, kutoen sukkaa, Esko rahin vieressä, neuloen).");
        
        assertEquals(90, DocumentRepositoryImpl.getIndex(builder.toString(), "es", 1));
        assertEquals(164, DocumentRepositoryImpl.getIndex(builder.toString(), "es", 2));
        assertEquals(200, DocumentRepositoryImpl.getIndex(builder.toString(), "es", 3));
    }
    
    @Test
    public void longText2(){
        StringBuilder builder = new StringBuilder();
        builder.append("(Topiaksen huone: perällä ovi ja akkuna, oikealla pöytä, ");
        builder.append("vasemmalla sivu-ovi ja enemmän edessä samalla sivulla");
        
        assertEquals(90, DocumentRepositoryImpl.getIndex(builder.toString(), "es", 1));
        assertEquals(-1, DocumentRepositoryImpl.getIndex(builder.toString(), "es", 2));
        assertEquals(-1, DocumentRepositoryImpl.getIndex(builder.toString(), "es", 3));
    }

}
