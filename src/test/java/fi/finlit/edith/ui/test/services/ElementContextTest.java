/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.*;

import org.junit.Test;

import fi.finlit.edith.ui.services.ElementContext;

/**
 * ElementContextTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class ElementContextTest {
    
    @Test
    public void teiHeader(){
        ElementContext context = new ElementContext(3);
        context.push("TEI");
        assertNull(context.getPath());
        context.push("teiHeader");
        assertNull(context.getPath());
        context.push("fileDesc");
        assertNull(context.getPath());
        context.push("titleStmt");
        assertEquals("titleStmt", context.getPath());
        context.push("title");
        assertEquals("titleStmt-title", context.getPath());
        context.pop();
        context.push("title");
        assertEquals("titleStmt-title2", context.getPath());
    }
    
    @Test
    public void text(){
        ElementContext context = new ElementContext(3);
        context.push("TEI");
        context.push("text");
        context.push("body");
        context.push("play");
        context.push("act");
        context.push("sp");
        assertEquals("play-act-sp", context.getPath());
    }

}
