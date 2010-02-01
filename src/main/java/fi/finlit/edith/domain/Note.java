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
public class Note extends Identifiable{
    
    @Predicate
    private Document document;

    @Predicate
    private NoteRevision latestRevision;

    /**
     * the id of the note in the context of the TEI document
     */
    @Predicate
    private String localId;

    @Predicate
    private NoteStatus status;

    @Predicate(ln = "tagged")
    private Set<Tag> tags;

    @Predicate
    private Term term;

    public Document getDocument() {
        return document;
    }

    public NoteRevision getLatestRevision() {
        return latestRevision;
    }

    public String getLocalId() {
        return localId;
    }

    public NoteStatus getStatus() {
        return status;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public Term getTerm() {
        return term;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setLatestRevision(NoteRevision latestRevision) {
        this.latestRevision = latestRevision;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public void setStatus(NoteStatus status) {
        this.status = status;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public void setTerm(Term term) {
        this.term = term;
    }
    
    
}
