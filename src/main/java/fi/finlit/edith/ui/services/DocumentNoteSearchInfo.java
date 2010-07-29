package fi.finlit.edith.ui.services;

import java.util.HashSet;
import java.util.Set;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.NoteFormat;
import fi.finlit.edith.domain.NoteType;
import fi.finlit.edith.domain.UserInfo;

/**
 * @author tiwe
 *
 */
public class DocumentNoteSearchInfo {

    private Document document;

    private Set<NoteType> noteTypes = new HashSet<NoteType>();

    private Set<NoteFormat> noteFormats = new HashSet<NoteFormat>();
    
    private Set<UserInfo> creators = new HashSet<UserInfo>();

    public DocumentNoteSearchInfo() {
        document = null;
        noteTypes = new HashSet<NoteType>();
        noteFormats = new HashSet<NoteFormat>();
        creators = new HashSet<UserInfo>();
    }

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

    public Set<UserInfo> getCreators() {
        return creators;
    }

    public void setCreators(Set<UserInfo> creators) {
        this.creators = creators;
    }



}
