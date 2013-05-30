package com.mysema.edith.dto;

public class FullDocumentNoteTO extends AbstractDocumentNoteTO {

    private DocumentTO document;

    private NoteTO note;

    public DocumentTO getDocument() {
        return document;
    }

    public void setDocument(DocumentTO document) {
        this.document = document;
    }

    public NoteTO getNote() {
        return note;
    }

    public void setNote(NoteTO note) {
        this.note = note;
    }

}
