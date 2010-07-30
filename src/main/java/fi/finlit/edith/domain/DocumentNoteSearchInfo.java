package fi.finlit.edith.domain;

import java.util.HashSet;
import java.util.Set;


/**
 * @author tiwe
 *
 */
public class DocumentNoteSearchInfo {

    private Set<Document> documents = new HashSet<Document>();

    private Set<NoteType> noteTypes = new HashSet<NoteType>();

    private Set<NoteFormat> noteFormats = new HashSet<NoteFormat>();
    
    private Set<UserInfo> creators = new HashSet<UserInfo>();

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
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

    public Set<UserInfo> getCreators() {
        return creators;
    }

    public void setCreators(Set<UserInfo> creators) {
        this.creators = creators;
    }



}
