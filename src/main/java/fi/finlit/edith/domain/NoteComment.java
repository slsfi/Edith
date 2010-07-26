package fi.finlit.edith.domain;

import org.joda.time.DateTime;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public class NoteComment extends Identifiable {
    @Predicate(ln="commentOf")
    private Note note;

    @Predicate
    private String message;

    @Predicate
    private String username;

    @Predicate
    private DateTime createdAt;

    public NoteComment() {
    }

    public NoteComment(Note note, String message, String username) {
        this.note = note;
        this.message = message;
        this.username = username;
        createdAt = new DateTime();
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Comment [message=" + message + "]";
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}
