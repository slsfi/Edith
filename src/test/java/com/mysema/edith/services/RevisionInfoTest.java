/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mysema.edith.dto.RevisionTO;

public class RevisionInfoTest {
    
    private RevisionTO ri;
    
    private RevisionTO ri2;

    @Before
    public void setUp() {
        ri = new RevisionTO(100, "today", "somebody");
        ri2 = new RevisionTO(100, "yesterday", "somebody else");
    }

    @Test
    public void Equals() {
        assertTrue(ri.equals(ri2));
    }

    @Test
    public void HashCode() {
        assertEquals(ri.hashCode(), ri2.hashCode());
    }

    @Test
    public void RevisionInfo_Long() {
        RevisionTO revisionInfo = new RevisionTO(666);
        assertEquals("", revisionInfo.getCreated());
        assertEquals("", revisionInfo.getCreator());
    }
}
