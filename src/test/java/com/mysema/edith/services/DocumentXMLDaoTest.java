/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class DocumentXMLDaoTest {
    
    @Test
    public void ShortText(){
        assertEquals(8, DocumentXMLDaoImpl.getIndex("This is a sentence", 'a', 0));
        assertEquals(5, DocumentXMLDaoImpl.getIndex("This is a sentence", 'i', 1));
        assertEquals(-1, DocumentXMLDaoImpl.getIndex("This is a sentence", 'b', 0));

        assertEquals(5, DocumentXMLDaoImpl.getIndex("This is a is ", 'i', 1));
        assertEquals(10, DocumentXMLDaoImpl.getIndex("This is a is ", 'i', 2));

        assertEquals(0, DocumentXMLDaoImpl.getIndex("This is a", 'T', 0));
        assertEquals(8, DocumentXMLDaoImpl.getIndex("This is a", 'a', 0));
        assertEquals(3, DocumentXMLDaoImpl.getIndex("12 123", '1', 1));
    }

    @Test
    public void LongText1(){
        StringBuilder builder = new StringBuilder();
        builder.append("(Topiaksen huone: perällä ovi ja akkuna, oikealla pöytä, ");
        builder.append("vasemmalla sivu-ovi ja enemmän edessä samalla sivulla rahi ja siinä suutarin ");
        builder.append("kaluja. Jaana istuu pöydän ääressä, kutoen sukkaa, Esko rahin vieressä, neuloen).");

        assertEquals(90, DocumentXMLDaoImpl.getIndex(builder.toString(), 'e', 8));
        assertEquals(164, DocumentXMLDaoImpl.getIndex(builder.toString(), 'e', 9));
        assertEquals(200, DocumentXMLDaoImpl.getIndex(builder.toString(), 'e', 12));
    }

    @Test
    public void LongText2(){
        StringBuilder builder = new StringBuilder();
        builder.append("(Topiaksen huone: perällä ovi ja akkuna, oikealla pöytä, ");
        builder.append("vasemmalla sivu-ovi ja enemmän edessä samalla sivulla");

        assertEquals(90, DocumentXMLDaoImpl.getIndex(builder.toString(), 'e', 8));
        assertEquals(-1, DocumentXMLDaoImpl.getIndex(builder.toString(), 'e', 9));
        assertEquals(-1, DocumentXMLDaoImpl.getIndex(builder.toString(), 'e', 10));
    }

}
