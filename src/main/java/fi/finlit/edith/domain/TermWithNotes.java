/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import java.util.HashSet;
import java.util.Set;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

/**
 * TermWithNotes provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns = EDITH.NS, ln = "Term")
public class TermWithNotes {

    @Predicate
    private String basicForm;

    @Predicate
    private String meaning;

    @Predicate(ln = "term", inv = true)
    private Set<Note> notes;

    public String getBasicForm() {
        return basicForm;
    }

    public String getMeaning() {
        return meaning;
    }

    public Set<Note> getNotes() {
        return notes;
    }

    public Set<Note> getUndeletedNotes() {
        return new HashSet<Note>(notes);
    }

    public void setBasicForm(String basicForm) {
        this.basicForm = basicForm;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setNotes(Set<Note> notes) {
        this.notes = notes;
    }

}
