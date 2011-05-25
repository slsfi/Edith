package fi.finlit.edith.ui.components.note;

import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.SelectedText;
import fi.finlit.edith.ui.pages.document.Annotate;
import fi.finlit.edith.ui.services.DocumentNoteRepository;
import fi.finlit.edith.ui.services.DocumentRepository;

public class DocumentNoteForm {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Parameter
    @Property
    private DocumentNote documentNoteOnEdit;

    @InjectPage
    private Annotate page;

    private SelectedText updateLongTextSelection;

    @Inject
    private DocumentNoteRepository documentNoteRepository;

    @Inject
    private DocumentRepository documentRepository;

    private boolean delete;

    public SelectedText getUpdateLongTextSelection() {
        if (updateLongTextSelection == null) {
            updateLongTextSelection = new SelectedText();
        }
        return updateLongTextSelection;
    }

    public void setUpdateLongTextSelection(SelectedText updateLongTextSelection) {
        this.updateLongTextSelection = updateLongTextSelection;
    }

    void onPrepareFromDocumentNoteForm(String docNoteId) {
        if (documentNoteOnEdit == null) {
            documentNoteOnEdit = documentNoteRepository.getById(docNoteId);
        }
    }

    void onSelectedFromDelete() {
        delete = true;
    }

    Object onSuccessFromDocumentNoteForm() {
        try {

            DocumentRevision documentRevision = page.getDocumentRevision();
            String successMsg = "submit-success";

            if (delete) {
                documentRevision = documentRepository.removeNotes(documentRevision, documentNoteOnEdit);
                page.getNoteEdit().setNoteOnEdit(documentNoteOnEdit.getNote());
                successMsg = "delete-success";
            } else {
                if (updateLongTextSelection.isValid()) {
                    documentNoteOnEdit = documentRepository.updateNote(documentNoteOnEdit, updateLongTextSelection);
                } else {
                    documentNoteRepository.save(documentNoteOnEdit);
                }
                page.getDocumentNotes().setSelectedNote(documentNoteOnEdit);
                page.getNoteEdit().setDocumentNoteOnEdit(documentNoteOnEdit);
            }

            page.getDocumentNotes().setNoteId(documentNoteOnEdit.getNote().getId());
            page.getDocumentRevision().setRevision(documentRevision.getRevision());
            
            page.getInfoMessage().addInfoMsg(successMsg);
            return new MultiZoneUpdate("noteEditZone", page.getNoteEdit().getBlock())
                    .add("infoMessageZone", page.getInfoMessage().getBlock())
                    .add("documentZone", page.getDocumentView())
                    .add("documentNotesZone", page.getDocumentNotes().getBlock());

        } catch (Exception e) {
            logger.error("Update long text failed", e);
            page.getInfoMessage().addInfoMsg("note-edition-failed");
            return new MultiZoneUpdate("infoMessageZone", page.getInfoMessage().getBlock());
        }

    }

}
