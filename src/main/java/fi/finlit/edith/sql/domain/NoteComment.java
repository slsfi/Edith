package fi.finlit.edith.sql.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class NoteComment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Note note;

    private String message;

    private String username;
    
    public NoteComment() { }

    public NoteComment(Note note, String message, String username) {
        this.note = note;
        this.message = message;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
