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

    private OrderBy orderBy = OrderBy.LEMMA;

    private boolean ascending = true;

    private boolean orphans = false;

    private Document currentDocument;

    public DocumentNoteSearchInfo() {}
    
    public DocumentNoteSearchInfo(Document document) {
        this.currentDocument = document;
    }
    
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

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public boolean isOrphans() {
        return orphans;
    }

    public void setOrphans(boolean orphans) {
        this.orphans = orphans;
    }

    public void setCurrentDocument(Document currentDocument) {
        this.currentDocument = currentDocument;
    }

    public Document getCurrentDocument() {
        return currentDocument;
    }
}
