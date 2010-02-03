/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import com.mysema.query.annotations.QueryInit;
import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

/**
 * NoteRevision provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITH.NS)
public class NoteRevision extends Identifiable{
    
    @Predicate
    private UserInfo createdBy; 
    
    @Predicate
    private long createdOn;
    
    @Predicate
    private String description;
    
    @Predicate(ln="latestRevision", inv=true)
    private Note latestRevisionOf;

    @Predicate
    private String lemma; 

    @Predicate
    private String longText; 
       
    @Predicate
    @QueryInit({"*", "term.meaning"})
    private Note revisionOf;
    
    @Predicate
    private long svnRevision;
    
    @Predicate
    private boolean deleted;
    
    // NOTE : not persisted
    private DocumentRevision docRevision;
    
    public NoteRevision createCopy() {        
        NoteRevision copy = new NoteRevision();
        copy.setDescription(description);
        copy.setLemma(lemma);
        copy.setLongText(longText);
        copy.setRevisionOf(revisionOf);
        copy.setSVNRevision(svnRevision);
        return copy;
        
    }

    public DocumentRevision getDocumentRevision(){
        if (docRevision == null || docRevision.getRevision() != svnRevision){
            docRevision = getRevisionOf().getDocument().getRevision(svnRevision);
        }
        return docRevision;
    }

    public UserInfo getCreatedBy() {
        return createdBy;
    }

    public long getCreatedOn() {
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

    public Note getRevisionOf() {
        return revisionOf;
    }

    public long getSvnRevision() {
        return svnRevision;
    }

    public void setCreatedBy(UserInfo createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedOn(long created) {
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

    public void setRevisionOf(Note revisionOf) {
        this.revisionOf = revisionOf;
    }

    public void setSVNRevision(long svnRevision) {
        this.svnRevision = svnRevision;
    }
 
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    public String getLocalId(){
        return revisionOf.getLocalId();
    }
    
//    public Document getDocument(){
//        return revisionOf.getDocument();
//    }
 
}
