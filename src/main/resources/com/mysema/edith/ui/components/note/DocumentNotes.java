package com.mysema.edith.ui.components.note;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.services.DocumentNoteDao;
import com.mysema.edith.ui.pages.document.Annotate;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@Import(library = { "classpath:js/jquery.scrollTo-min.js" })
public class DocumentNotes {

    @InjectPage
    private Annotate page;

    @Inject
    private Block documentNotesBlock;

    private List<DocumentNote> documentNotes;

    private Long noteId;

    @Inject
    private DocumentNoteDao documentNoteDao;

    @Property
    private DocumentNote documentNote;

    private DocumentNote selectedNote;

    public Block getBlock() {
        return documentNotesBlock;
    }

    private final Comparator<DocumentNote> byPosition = new Comparator<DocumentNote>() {
        @Override
        public int compare(DocumentNote n1, DocumentNote n2) {
            return n1.getPosition() - n2.getPosition();
        }
    };

    public List<DocumentNote> getDocumentNotes() {
        if (documentNotes == null) {
            documentNotes = documentNoteDao.getOfNote(noteId);
            Collections.sort(documentNotes, byPosition);
        }

        return documentNotes;
    }

    public DocumentNote getSelectedNote() {
        if (selectedNote == null) {
            getDocumentNotes();
            selectedNote = documentNotes.size() > 0 ? documentNotes.get(0) : null;
        }
        return selectedNote;
    }

    public void setSelectedNote(DocumentNote selectedNote) {
        this.selectedNote = selectedNote;
    }

    public String getSelectedCssClass() {
        return documentNote.getId().equals(getSelectedNote().getId()) ? "selected-note" : "";
    }

    Object onActionFromSelectDocumentNote(long documentNoteId) {
        selectedNote = documentNoteDao.getById(documentNoteId);

        page.getNoteEdit().setDocumentNoteOnEdit(selectedNote);

        return page.getNoteEdit().getBlock();
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

}
