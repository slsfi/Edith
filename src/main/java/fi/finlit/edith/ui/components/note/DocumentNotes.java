package fi.finlit.edith.ui.components.note;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.ui.pages.document.Annotate;
import fi.finlit.edith.ui.services.DocumentNoteRepository;

@Import(library = { "classpath:js/jquery.scrollTo-min.js" })
@SuppressWarnings("unused")
public class DocumentNotes {

    @InjectPage
    private Annotate page;

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
    
    private Comparator<DocumentNote> byPosition = new Comparator<DocumentNote>() {
        public int compare(DocumentNote n1, DocumentNote n2) {
            return n1.getPosition() - n2.getPosition();
        }
    };

    public List<DocumentNote> getDocumentNotes() {
        if (documentNotes == null) {
            documentNotes = documentNoteRepository.getOfNote(noteId);
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
    
    Object onActionFromSelectDocumentNote(String documentNoteId) {
        System.out.println("select documentNote " + documentNoteId);

        selectedNote = documentNoteRepository.getById(documentNoteId);

        page.getNoteEdit().setDocumentNoteOnEdit(selectedNote);
        
        return page.getNoteEdit().getBlock();
    }

    public void setNoteId(String noteId) {
        System.out.println("Setting noteid to " + noteId);
        this.noteId = noteId;
    }
    
    public String getLocalId() {
        return getSelectedNote() != null ? getSelectedNote().getLocalId() : "";
    }


}
