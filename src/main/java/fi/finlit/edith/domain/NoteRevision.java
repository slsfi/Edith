/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import java.util.Set;

import org.joda.time.DateTime;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

/**
 * Note provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITH.NS)
public class NoteRevision extends Identifiable {
    
    @Predicate
    private String basicForm; 
    
    @Predicate
    private User createdBy;
    
    @Predicate
    private DateTime createdOn;
    
    @Predicate
    private String description;
    
    @Predicate(ln="latestRevision", inv=true)
    private Note latestRevisionOf;

    @Predicate
    private String lemma; 

    @Predicate
    private String longText; 
    
    @Predicate
    private Term term;
        
    @Predicate
    private long revision;
    
    @Predicate
    private Note revisionOf;

    @Predicate
    private NoteStatus status;
    
    @Predicate(ln="tagged")
    private Set<Tag> tags;

    public String getBasicForm() {
        return basicForm;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public DateTime getCreatedOn() {
        return createdOn;
    }

    public String getDescription() {
        return description;
    }

    public Note getLatestRevisionOf() {
        return latestRevisionOf;
    }
    
    public String getLemma() {
        return lemma;
    }
    
    public String getLongText() {
        return longText;
    }

    public Term getTerm() {
        return term;
    }

    public long getRevision() {
        return revision;
    }

    public Note getRevisionOf() {
        return revisionOf;
    }

    public NoteStatus getStatus() {
        return status;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setBasicForm(String basicForm) {
        this.basicForm = basicForm;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedOn(DateTime created) {
        this.createdOn = created;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public void setLongText(String longText) {
        this.longText = longText;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public void setRevision(long svnRevision) {
        this.revision = svnRevision;
    }

    public void setRevisionOf(Note revisionOf) {
        this.revisionOf = revisionOf;
    }

    public void setStatus(NoteStatus status) {
        this.status = status;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
 
}
