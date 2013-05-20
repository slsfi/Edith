package com.mysema.edith.web;

public class DocNoteTO {
    
    private Long id;
    
    private Object note, document;
    
    private String path, fullSelection;

    public Object getDocument() {
        return document;
    }

    public String getFullSelection() {
        return fullSelection;
    }

    public Long getId() {
        return id;
    }

    public Object getNote() {
        return note;
    }

    public String getPath() {
        return path;
    }

    public void setDocument(Object document) {
        this.document = document;
    }

    public void setFullSelection(String fullSelection) {
        this.fullSelection = fullSelection;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNote(Object note) {
        this.note = note;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    
    
    
}