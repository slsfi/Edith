package fi.finlit.edith.ui.components.note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Concept;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteComment;
import fi.finlit.edith.domain.Term;
import fi.finlit.edith.dto.DocumentRevision;
import fi.finlit.edith.dto.NoteCommentComparator;
import fi.finlit.edith.dto.SelectedText;
import fi.finlit.edith.ui.components.InfoMessage;
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
    private NoteDao noteRepository;

    @Inject
    private DocumentNoteDao documentNoteRepository;

    private DocumentNote documentNoteOnEdit;

    private Note noteOnEdit;

    @Property
    private Note loopNote;

    @Property
    private List<DocumentNote> selectedNotes;

    @InjectComponent
    private Comments comments;

    public String getNoteId() {
        return documentNoteOnEdit != null ? documentNoteOnEdit.getId() : null;
    }

    public Block getBlock() {
        return noteEditBlock;
    }

    public boolean isSlsMode() {
        return page.isSlsMode();
    }

    public Concept getLoopNoteConcept() {
        return loopNote.getConcept(page.isSlsMode());
    }


    public int getLemmaInstances() {
        return documentNoteRepository.getDocumentNoteCount(documentNoteOnEdit.getNote());
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
