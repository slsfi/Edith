package fi.finlit.edith.ui.components.note;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.dto.DocumentRevision;
import fi.finlit.edith.dto.SelectedText;
import fi.finlit.edith.ui.pages.document.Annotate;
import fi.finlit.edith.ui.services.DocumentNoteRepository;
import fi.finlit.edith.ui.services.DocumentRepository;

public class DocumentNoteForm {

    //private final Logger logger = LoggerFactory.getLogger(getClass());

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

            String successMsg = "submit-success";
            String noteId = documentNoteOnEdit.getNote().getId();
            
            if (delete) {
                DocumentRevision documentRevision = documentRepository.removeNotes(
                        page.getDocumentRevision(), documentNoteOnEdit);
                page.getNoteEdit().setNoteOnEdit(documentNoteOnEdit.getNote());
                page.getNoteEdit().setDocumentNoteOnEdit(null);
                page.getDocumentRevision().setRevision(documentRevision.getRevision());
                successMsg = "delete-success";
            } else {
                if (updateLongTextSelection.isValid()) {
                    documentNoteOnEdit = documentRepository.updateNote(documentNoteOnEdit, updateLongTextSelection);
                } else {
                    documentNoteRepository.save(documentNoteOnEdit);
                }
                page.getDocumentNotes().setSelectedNote(documentNoteOnEdit);
                page.getNoteEdit().setDocumentNoteOnEdit(documentNoteOnEdit);
                page.getDocumentRevision().setRevision(documentNoteOnEdit.getSVNRevision());
            }

            page.getDocumentNotes().setNoteId(noteId);
            
            return page.zoneWithInfo(successMsg)
                .add("noteEditZone", page.getNoteEdit().getBlock())
                .add("documentZone", page.getDocumentView())
                .add("documentNotesZone", page.getDocumentNotes().getBlock());
                

        } catch (Exception e) {
            return page.zoneWithError("note-edition-failed", e);
        }

    }

}
