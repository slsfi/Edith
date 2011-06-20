package fi.finlit.edith.sql.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.mysema.query.annotations.QueryInit;

@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class DocumentNote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Document document;

    private boolean publishable;

    private Long revision;

    private boolean deleted;

    private String fullSelection;

    private int position;

    private long createdOn;

    private String shortenedSelection;
    
    private String lemmaPosition;

    @ManyToOne
    @QueryInit("*")
    private Note note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public boolean isPublishable() {
        return publishable;
    }

    public void setPublishable(boolean publishable) {
        this.publishable = publishable;
    }

    public Long getRevision() {
        return revision;
    }

    public void setRevision(Long revision) {
        this.revision = revision;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setFullSelection(String fullSelection) {
        this.fullSelection = fullSelection;
    }

    public String getFullSelection() {
        return fullSelection;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public String getShortenedSelection() {
        return shortenedSelection;
    }

    public void setShortenedSelection(String shortenedSelection) {
        this.shortenedSelection = shortenedSelection;
    }
    
    public String getLemmaPosition() {
        return lemmaPosition;
    }
    
    public void setLemmaPosition(String lemmaPosition) {
        this.lemmaPosition = lemmaPosition;
    }
}
