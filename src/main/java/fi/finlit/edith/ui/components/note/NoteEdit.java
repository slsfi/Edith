package fi.finlit.edith.ui.components.note;

import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.Concept;
import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.sql.domain.Note;
import fi.finlit.edith.ui.pages.document.Annotate;
import fi.finlit.edith.ui.services.DocumentNoteDao;
import fi.finlit.edith.ui.services.NoteDao;

@SuppressWarnings("unused")
public class NoteEdit {

    @InjectPage
    private Annotate page;

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

    public Long getNoteId() {
        return documentNoteOnEdit != null ? documentNoteOnEdit.getId() : null;
    }

    public Block getBlock() {
        return noteEditBlock;
    }

    public boolean isSlsMode() {
        return page.isSlsMode();
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
