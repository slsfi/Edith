/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.editio.domain;

import org.joda.time.DateTime;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.editio.EDITIO;

/**
 * Comment provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITIO.NS)
public class NoteComment extends Identifiable{
    
    @Predicate
    private DateTime createdOn;
    
    @Predicate
    private User createdBy;

    @Predicate
    private String text;

    @Predicate
    private String title;

    @Predicate
    private Note note;
    
    public User getCreatedBy() {
        return createdBy;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public DateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(DateTime createdOn) {
        this.createdOn = createdOn;
    }
    
    
    
    

}
