/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.mysema.edith.util.ElementContext;


public class ElementContextTest {

    @Test
    public void Push() {
        ElementContext context = new ElementContext(3);
        context.push("baz");
        context.push("bar");
        context.push("barbazzz");
        context.push("foo");
        assertEquals("foo", context.getPath());
        context.push("foobar");
        assertEquals("foo-foobar", context.getPath());
    }

    @Test
    public void Pop() {
        ElementContext context = new ElementContext(3);
        context.push("baz");
        context.push("bar");
        context.push("barbazzz");
        context.push("foo");
        assertEquals("foo", context.getPath());
        context.pop();
        assertEquals(null, context.getPath());
        context.push("baz");
        context.push("bar");
        assertEquals("baz-bar", context.getPath());
        context.pop();
        assertEquals("baz", context.getPath());
        context.push("bar");
        assertEquals("baz-bar2", context.getPath());
    }

    @Test
    public void GetPath() {
        ElementContext context = new ElementContext(0);
        context.push("baz");
        context.push("bar");
        context.push("barbazzz");
        context.push("foo");
        assertEquals("baz-bar-barbazzz-foo", context.getPath());
        context.push("foobar");
        assertEquals("baz-bar-barbazzz-foo-foobar", context.getPath());
    }

    @Test
    public void Clone() throws Exception {
        ElementContext context = new ElementContext(0);
        context.push("foo");
        context.push("bar");
        assertEquals("foo-bar", context.getPath());
        ElementContext clonedContext = (ElementContext) context.clone();
        assertEquals(context.getPath(), clonedContext.getPath());
        context.pop();
        context.push("bar"); // bar will be bar2 in context
        assertThat(context.getPath(), not(equalTo(clonedContext.getPath())));
    }

    @Test
    public void TeiHeader(){
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
