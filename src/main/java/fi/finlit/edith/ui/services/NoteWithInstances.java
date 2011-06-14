package fi.finlit.edith.ui.services;

import java.util.Collection;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.Note;


/**
 * @author tiwe
 *
 */
@Deprecated //This probaly can be removed
public class NoteWithInstances {
    
    private final Note note;
    
    private final Collection<DocumentNote> documentNotes;

    public NoteWithInstances(Note note, Collection<DocumentNote> documentNotes) {
        this.note = note;
        for (DocumentNote dn : documentNotes){
            if (dn == null) throw new IllegalArgumentException();
        }
        this.documentNotes = documentNotes;
    }
    
    public Note getNote() {
        return note;
    }

    public Collection<DocumentNote> getDocumentNotes() {
        return documentNotes;
    }
    
}
