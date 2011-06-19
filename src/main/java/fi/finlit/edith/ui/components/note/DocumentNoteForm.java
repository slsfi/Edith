package fi.finlit.edith.ui.components.note;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.dto.DocumentRevision;
import fi.finlit.edith.dto.SelectedText;
import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.ui.pages.document.Annotate;
import fi.finlit.edith.ui.services.DocumentDao;
import fi.finlit.edith.ui.services.DocumentNoteDao;

public class DocumentNoteForm {

    // private final Logger logger = LoggerFactory.getLogger(getClass());

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
