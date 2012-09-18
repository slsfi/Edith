package com.mysema.edith.ui.components.note;

import java.util.List;

import com.mysema.edith.EDITH;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.services.DocumentNoteDao;
import com.mysema.edith.services.NoteDao;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@SuppressWarnings("unused")
public class NoteEdit {
    @Inject
    private Block noteEditBlock;

    @Inject
    private NoteDao noteDao;

    @Inject
    private DocumentNoteDao documentNoteDao;

    private DocumentNote documentNoteOnEdit;

    private Note noteOnEdit;

    @Property
    private Note loopNote;

    @Property
    private List<DocumentNote> selectedNotes;

    @InjectComponent
    private Comments comments;

    @Inject
    @Symbol(EDITH.EXTENDED_TERM)
    private boolean slsMode;

    public Long getNoteId() {
        return documentNoteOnEdit != null ? documentNoteOnEdit.getId() : null;
    }

    public Block getBlock() {
        return noteEditBlock;
    }

    public boolean isSlsMode() {
        return slsMode;
    }

    public int getLemmaInstances() {
        return documentNoteDao.getDocumentNoteCount(documentNoteOnEdit.getNote());
    }

    public void setNoteOnEdit(Note noteOnEdit) {
        this.noteOnEdit = noteOnEdit;
    }

    public Note getNoteOnEdit() {
        return noteOnEdit;
    }

    public void setDocumentNoteOnEdit(DocumentNote documentNoteOnEdit) {
        this.documentNoteOnEdit = documentNoteOnEdit;
        if (documentNoteOnEdit != null) {
            setNoteOnEdit(documentNoteOnEdit.getNote());
        }
    }

    public DocumentNote getDocumentNoteOnEdit() {
        return documentNoteOnEdit;
    }

    public Comments getComments() {
        return comments;
    }

}
