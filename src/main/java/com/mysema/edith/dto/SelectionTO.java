package com.mysema.edith.dto;

public class SelectionTO {
    
    private Long noteId;
    
    private Long documentId;
    
    private SelectedText text;

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public SelectedText getText() {
        return text;
    }

    public void setText(SelectedText text) {
        this.text = text;
    }
    
}
