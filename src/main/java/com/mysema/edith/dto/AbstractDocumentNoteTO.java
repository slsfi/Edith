package com.mysema.edith.dto;

public abstract class AbstractDocumentNoteTO {
    
    private Long id;

    private boolean publishable;

    private Long revision;

    private boolean deleted;

    private String fullSelection;

    private int position;

    private long createdOn;

    private String shortenedSelection;

    private String lemmaPosition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFullSelection() {
        return fullSelection;
    }

    public void setFullSelection(String fullSelection) {
        this.fullSelection = fullSelection;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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
