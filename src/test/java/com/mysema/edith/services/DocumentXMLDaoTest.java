/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class DocumentXMLDaoTest {
    
    @Test
    public void ShortText(){
        assertEquals(8, DocumentXMLDaoImpl.getIndex("This is a sentence", "a", 1));
        assertEquals(5, DocumentXMLDaoImpl.getIndex("This is a sentence", "is", 2));
        assertEquals(-1, DocumentXMLDaoImpl.getIndex("This is a sentence", "b", 1));

        assertEquals(5, DocumentXMLDaoImpl.getIndex("This is a is ", "is", 2));
        assertEquals(10, DocumentXMLDaoImpl.getIndex("This is a is ", "is", 3));

        assertEquals(0, DocumentXMLDaoImpl.getIndex("This is a", "This", 1));
        assertEquals(8, DocumentXMLDaoImpl.getIndex("This is a", "a", 1));
        assertEquals(3, DocumentXMLDaoImpl.getIndex("12 123", "123", 1));
    }

    @Test
    public void LongText1(){
        StringBuilder builder = new StringBuilder();
        builder.append("(Topiaksen huone: perällä ovi ja akkuna, oikealla pöytä, ");
        builder.append("vasemmalla sivu-ovi ja enemmän edessä samalla sivulla rahi ja siinä suutarin ");
        builder.append("kaluja. Jaana istuu pöydän ääressä, kutoen sukkaa, Esko rahin vieressä, neuloen).");

        assertEquals(90, DocumentXMLDaoImpl.getIndex(builder.toString(), "es", 1));
        assertEquals(164, DocumentXMLDaoImpl.getIndex(builder.toString(), "es", 2));
        assertEquals(200, DocumentXMLDaoImpl.getIndex(builder.toString(), "es", 3));
    }

    @Test
    public void LongText2(){
        StringBuilder builder = new StringBuilder();
        builder.append("(Topiaksen huone: perällä ovi ja akkuna, oikealla pöytä, ");
        builder.append("vasemmalla sivu-ovi ja enemmän edessä samalla sivulla");

        assertEquals(90, DocumentXMLDaoImpl.getIndex(builder.toString(), "es", 1));
        assertEquals(-1, DocumentXMLDaoImpl.getIndex(builder.toString(), "es", 2));
        assertEquals(-1, DocumentXMLDaoImpl.getIndex(builder.toString(), "es", 3));
    }

}
