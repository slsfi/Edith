package fi.finlit.edith.domain;

import com.mysema.query.annotations.QueryDelegate;

public class QueryDelegates {
    
    @QueryDelegate(DocumentNote.class)
    public static QConcept concept(QDocumentNote documentNote, boolean extendedTerm) {
        if (extendedTerm) {
            return documentNote.note().term().concept();
        } else {
            return documentNote.note().concept();
        }
    }
    
    @QueryDelegate(Note.class)
    public static QConcept concept(QNote note, boolean extendedTerm) {
        if (extendedTerm) {
            return note.term().concept();
        } else {
            return note.concept();
        }
    }

}
