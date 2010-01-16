/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import java.util.Set;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

/**
 * Note provides
 * 
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns = EDITH.NS)
public class Note extends Identifiable {

    /**
     * the id of the note in the context of the TEI document
     */
    @Predicate
    private String localId;

    @Predicate
    private Document document;

    @Predicate
    private NoteRevision latestRevision;

    @Predicate
    private Term term;

    @Predicate
    private NoteStatus status;

    @Predicate(ln = "tagged")
    private Set<Tag> tags;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public NoteRevision getLatestRevision() {
        return latestRevision;
    }

    public void setLatestRevision(NoteRevision latestRevision) {
        this.latestRevision = latestRevision;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public NoteStatus getStatus() {
        return status;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setStatus(NoteStatus status) {
        this.status = status;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
