/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import org.joda.time.DateTime;

import com.mysema.query.annotations.QueryInit;
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
    private long svnRevision;
    
    @Predicate
    @QueryInit({"term.meaning", "latestRevision"})
    private Note revisionOf;
    
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

    public long getSvnRevision() {
        return svnRevision;
    }

    public Note getRevisionOf() {
        return revisionOf;
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

    public void setSVNRevision(long svnRevision) {
        this.svnRevision = svnRevision;
    }

    public void setRevisionOf(Note revisionOf) {
        this.revisionOf = revisionOf;
    }

 
}
