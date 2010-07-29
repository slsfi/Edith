package fi.finlit.edith.ui.services;

import java.util.Set;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.NoteFormat;
import fi.finlit.edith.domain.NoteType;
import fi.finlit.edith.domain.User;

public class DocumentNoteSearchInfo {
    
    private Document document;
    
    private Set<NoteType> noteTypes;
    
    private Set<NoteFormat> noteFormats;
    
    private Set<User> creators;

    // TODO : sort criteria
       
    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Set<NoteType> getNoteTypes() {
        return noteTypes;
    }

    public void setNoteTypes(Set<NoteType> noteTypes) {
        this.noteTypes = noteTypes;
    }

    public Set<NoteFormat> getNoteFormats() {
        return noteFormats;
    }

    public void setNoteFormats(Set<NoteFormat> noteFormats) {
        this.noteFormats = noteFormats;
    }

    public Set<User> getCreators() {
        return creators;
    }

    public void setCreators(Set<User> creators) {
        this.creators = creators;
    }
    
    

}
