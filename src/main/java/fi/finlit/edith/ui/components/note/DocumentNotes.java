package fi.finlit.edith.ui.components.note;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.ui.pages.document.Annotate;
import fi.finlit.edith.ui.services.DocumentNoteDao;

@Import(library = { "classpath:js/jquery.scrollTo-min.js" })
@SuppressWarnings("unused")
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
            System.err.println("selectednote null, selecting first");
            getDocumentNotes();
            selectedNote = documentNotes.size() > 0 ? documentNotes.get(0) : null;
        }
        return selectedNote;
    }

    public void setSelectedNote(DocumentNote selectedNote) {
        this.selectedNote = selectedNote;
    }

    public String getSelectedCssClass() {
        System.out.println("selecting" + documentNote.getId() + " == " + getSelectedNote().getId() );
        return documentNote.getId().equals(getSelectedNote().getId()) ? "selected-note" : "";
    }

    Object onActionFromSelectDocumentNote(long documentNoteId) {
        System.out.println("select documentNote " + documentNoteId);

        selectedNote = documentNoteDao.getById(documentNoteId);

        page.getNoteEdit().setDocumentNoteOnEdit(selectedNote);

        return page.getNoteEdit().getBlock();
    }

    public void setNoteId(Long noteId) {
        System.out.println("Setting noteid to " + noteId);
        this.noteId = noteId;
    }


}
