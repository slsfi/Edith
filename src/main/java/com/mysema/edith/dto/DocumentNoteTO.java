package com.mysema.edith.dto;

public class DocumentNoteTO extends AbstractDocumentNoteTO {

    private Long document;

    private Long note;

    public Long getDocument() {
        return document;
    }

    public void setDocument(Long document) {
        this.document = document;
    }

    public Long getNote() {
        return note;
    }

    public void setNote(Long note) {
        this.note = note;
    }


}
