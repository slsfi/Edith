package com.mysema.edith.dto;

public class DocumentNoteListItemTO extends AbstractDocumentNoteTO {

    private DocumentTO document;

    private NoteTO note;

    private NoteCommentTO comment;

    public void setNote(NoteTO note) {
        this.note = note;
    }

    public void setDocument(DocumentTO document) {
        this.document = document;
    }

    public void setComment(NoteCommentTO comment) {
        this.comment = comment;
    }

    public NoteTO getNote() {
        return note;
    }

    public DocumentTO getDocument() {
        return document;
    }

    public NoteCommentTO getComment() {
        return comment;
    }
}
