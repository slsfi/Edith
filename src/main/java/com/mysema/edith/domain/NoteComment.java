/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "notecomment")
public class NoteComment extends BaseEntity {

    @ManyToOne
    private Note note;

    private String message;

    private String username;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdAt;

    public NoteComment() {
    }

    public NoteComment(Note note, String message, String username) {
        this.note = note;
        this.message = message;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public Note getNote() {
        return note;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }
}
