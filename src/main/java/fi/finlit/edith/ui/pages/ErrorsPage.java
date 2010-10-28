package fi.finlit.edith.ui.pages;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteRepository;
import fi.finlit.edith.domain.Note;

public class ErrorsPage {
    
    @SuppressWarnings("unused")
    @Inject
    private DocumentNoteRepository documentNoteRepository;
    
    @SuppressWarnings("unused")
    @Property
    private DocumentNote documentNote;

    public List<Note> getNotes(){
        return documentNoteRepository.getOrphans();
    }
    
    public List<DocumentNote> getDocumentNotes(){
        return documentNoteRepository.getNotesLessDocumentNotes();
    }
    
}
