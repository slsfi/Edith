package fi.finlit.edith.ui.pages;

import java.util.List;

import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteRepository;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.ui.services.svn.RevisionInfo;

@SuppressWarnings("unused")
@IncludeJavaScriptLibrary( { "classpath:jquery-1.4.1.js", "deleteDialog.js" })
public class ErrorsPage {
    
    @Inject
    private DocumentNoteRepository documentNoteRepository;
    
    @Inject
    private DocumentRepository documentRepository;
    
    @Property
    private DocumentNote documentNote;

    public List<Note> getNotes(){
        return documentNoteRepository.getOrphans();
    }
    
    public List<DocumentNote> getDocumentNotes(){
        return documentNoteRepository.getNotesLessDocumentNotes();
    }
    
    void onActionFromDelete(String documentNoteId) {
        DocumentNote note = documentNoteRepository.getById(documentNoteId);
        
        List<RevisionInfo> revs = documentRepository.getRevisions(note.getDocument());
        long last = revs.get(revs.size()-1).getSvnRevision();
        
        documentRepository.removeNotesPermanently(new DocumentRevision(note.getDocument(), last), note);
        
    }
}
