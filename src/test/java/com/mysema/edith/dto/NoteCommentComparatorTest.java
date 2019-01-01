/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.dto;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

import com.mysema.edith.domain.NoteComment;


public class NoteCommentComparatorTest {

    private static final int SMALLER = -1;
    private static final int EQUAL = 0;
    private static final int BIGGER = 1;

    @Test
    public void Is_Smaller_When_Ascending_Comparator() {
        NoteComment smaller = new NoteComment();
        smaller.setCreatedAt(new DateTime(0));
        NoteComment bigger = new NoteComment();
        bigger.setCreatedAt(new DateTime(10));
        assertEquals(SMALLER, NoteCommentComparator.ASC.compare(smaller, bigger));
    }

    @Test
    public void Is_Bigger_When_Ascending_Comparator() {
        NoteComment bigger = new NoteComment();
        bigger.setCreatedAt(new DateTime(10));
        NoteComment smaller = new NoteComment();
        smaller.setCreatedAt(new DateTime(0));
        assertEquals(BIGGER, NoteCommentComparator.ASC.compare(bigger, smaller));
    }

    @Test
    public void Is_Equal_When_Ascending_Comparator() {
        NoteComment comment = new NoteComment();
        comment.setCreatedAt(new DateTime(10));
        assertEquals(EQUAL, NoteCommentComparator.ASC.compare(comment, comment));
    }

}
