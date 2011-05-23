package fi.finlit.edith.ui.components.note;

import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.SelectedText;
import fi.finlit.edith.ui.pages.document.Annotate;
import fi.finlit.edith.ui.services.DocumentNoteRepository;
import fi.finlit.edith.ui.services.DocumentRepository;

public class DocumentNoteForm {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Parameter
    @Property
    private DocumentNote noteOnEdit;

    @InjectPage
    private Annotate page;

    private SelectedText updateLongTextSelection;

    @Inject
    private DocumentNoteRepository documentNoteRepository;

    @Inject
    private DocumentRepository documentRepository;

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
        if (noteOnEdit == null) {
            noteOnEdit = documentNoteRepository.getById(docNoteId);
        }
    }

    Object onSuccessFromDocumentNoteForm() {
        try {

            if (updateLongTextSelection.isValid()) {
                noteOnEdit = documentRepository.updateNote(noteOnEdit, updateLongTextSelection);
            } else {
                documentNoteRepository.save(noteOnEdit);
            }

            page.getDocumentRevision().setRevision(noteOnEdit.getDocumentRevision().getRevision());
            page.getNoteEdit().setNoteOnEdit(noteOnEdit);
            page.getInfoMessage().addInfoMsg("submit-success");
            return new MultiZoneUpdate("noteEditZone", page.getNoteEdit().getBlock()).add(
                    "infoMessageZone", page.getInfoMessage().getBlock()).add("documentZone",
                    page.getDocumentView());

        } catch (Exception e) {
            logger.error("Update long text failed", e);
            page.getInfoMessage().addInfoMsg("note-edition-failed");
            return new MultiZoneUpdate("infoMessageZone", page.getInfoMessage().getBlock());
        }

    }

}
