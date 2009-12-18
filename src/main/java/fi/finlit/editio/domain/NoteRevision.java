/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.editio.domain;

import java.util.Set;

import org.joda.time.DateTime;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.editio.EDITIO;

/**
 * Note provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITIO.NS)
public class NoteRevision extends Identifiable {
    
//  TODO  : subtekstinLahde, sitaatti, lahde;
    
    // Explanation
    @Predicate
    private String lemma;

    // Explanation
    @Predicate
    private String meaning; // merkitys
    
    // Explanation : subtekstinLahde
    
    // Explanation : sitaatti
    
    @Predicate(ln="validFor")
    private Set<Long> svnRevisions;
    
    @Predicate
    private String basicForm; // perusmuoto
    
    @Predicate
    private DateTime createdOn;
    
    @Predicate
    private User createdBy;
    
    @Predicate
    private String explanation; // selitys 

    @Predicate
    private Note revisionOf;
    
    @Predicate
    private NoteStatus status;
    
    @Predicate
    private String longText; // pitkä viite

    @Predicate(ln="tagged")
    private Set<Tag> tags;

    public String getBasicForm() {
        return basicForm;
    }

    public DateTime getCreatedOn() {
        return createdOn;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getLemma() {
        return lemma;
    }

    public String getLongText() {
        return longText;
    }
    
    public String getMeaning() {
        return meaning;
    }
    
    public Set<Tag> getTags() {
        return tags;
    }
    
    public void setBasicForm(String basicForm) {
        this.basicForm = basicForm;
    }

    public void setCreatedOn(DateTime created) {
        this.createdOn = created;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public void setLongText(String longText) {
        this.longText = longText;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<Long> getSvnRevisions() {
        return svnRevisions;
    }

    public void setSvnRevisions(Set<Long> svnRevisions) {
        this.svnRevisions = svnRevisions;
    }

    public Note getRevisionOf() {
        return revisionOf;
    }

    public void setRevisionOf(Note revisionOf) {
        this.revisionOf = revisionOf;
    }

    public NoteStatus getStatus() {
        return status;
    }

    public void setStatus(NoteStatus status) {
        this.status = status;
    }
 
    
}
