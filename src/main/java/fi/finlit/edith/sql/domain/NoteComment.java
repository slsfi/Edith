package fi.finlit.edith.sql.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "notecomment")
public class NoteComment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Note note;

    private String message;

    private String username;

    @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime createdAt;

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

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }
}
