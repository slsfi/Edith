package com.mysema.edith.ui.components.note;

import org.hibernate.annotations.Parameter;

import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.dto.SelectedText;
import com.mysema.edith.services.DocumentDao;
import com.mysema.edith.services.DocumentNoteDao;
import com.mysema.edith.ui.pages.document.Annotate;
import com.sun.xml.internal.ws.api.PropertySet.Property;

public class DocumentNoteForm {
    @Parameter
    @Property
    private DocumentNote documentNoteOnEdit;

    @InjectPage
    private Annotate page;

    private SelectedText updateLongTextSelection;

    @Inject
    private DocumentNoteDao documentNoteDao;

    @Inject
    private DocumentDao documentDao;

    private boolean delete;

    public boolean isSlsMode() {
        return page.isSlsMode();
    }

    public SelectedText getUpdateLongTextSelection() {
        if (updateLongTextSelection == null) {
            updateLongTextSelection = new SelectedText();
        }
        return updateLongTextSelection;
    }

    public void setUpdateLongTextSelection(SelectedText updateLongTextSelection) {
        this.updateLongTextSelection = updateLongTextSelection;
    }

    void onPrepareFromDocumentNoteForm(long docNoteId) {
        if (documentNoteOnEdit == null) {
            documentNoteOnEdit = documentNoteDao.getById(docNoteId);
        }
    }

    void onSelectedFromDelete() {
        delete = true;
    }

    Object onSuccessFromDocumentNoteForm() {
        try {

            String successMsg = "submit-success";
            long noteId = documentNoteOnEdit.getNote().getId();

            if (delete) {
                documentDao.removeDocumentNotes(page.getDocument(), documentNoteOnEdit);
                page.getNoteEdit().setNoteOnEdit(documentNoteOnEdit.getNote());
                page.getNoteEdit().setDocumentNoteOnEdit(null);
                successMsg = "delete-success";
            } else {
                if (updateLongTextSelection.isValid()) {
                    documentNoteOnEdit = documentDao.updateNote(documentNoteOnEdit,
                            updateLongTextSelection);
                } else {
                    documentNoteDao.save(documentNoteOnEdit);
                }
                page.getDocumentNotes().setSelectedNote(documentNoteOnEdit);
                page.getNoteEdit().setDocumentNoteOnEdit(documentNoteOnEdit);
            }

            page.getDocumentNotes().setNoteId(noteId);

            return page.zoneWithInfo(successMsg).add("noteEditZone", page.getNoteEdit().getBlock())
                    .add("documentZone", page.getDocumentView())
                    .add("documentNotesZone", page.getDocumentNotes().getBlock());

        } catch (Exception e) {
            return page.zoneWithError("note-edition-failed", e);
        }

    }

}
