package fi.finlit.edith.ui.components.note;

import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.ui.pages.document.Annotate;
import fi.finlit.edith.ui.services.DocumentNoteRepository;

@Import(library = { "classpath:js/jquery.scrollTo-min.js" })
@SuppressWarnings("unused")
public class DocumentNotes {

    @Parameter
    private NoteEdit noteEdit;

    @Inject
    private Block documentNotesBlock;

    private List<DocumentNote> documentNotes;

    private String noteId;

    @Inject
    private DocumentNoteRepository documentNoteRepository;

    @Property
    private DocumentNote documentNote;

    private DocumentNote selectedNote;

    public Block getBlock() {
        return documentNotesBlock;
    }

    public List<DocumentNote> getDocumentNotes() {
        if (documentNotes == null) {
            documentNotes = documentNoteRepository.getOfNote(noteId);
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

    Object onActionFromSelectDocumentNote(String documentNoteId) {
        System.out.println("select documentNote " + documentNoteId);

        selectedNote = documentNoteRepository.getById(documentNoteId);

        noteEdit.setNoteOnEdit(selectedNote);
        return noteEdit.getBlock();
    }

    public void setNoteId(String noteId) {
        System.out.println("Setting noteid to " + noteId);
        this.noteId = noteId;
    }

}
