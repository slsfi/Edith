package fi.finlit.edith.util;

import com.mysema.query.annotations.QueryDelegate;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.QConcept;
import fi.finlit.edith.domain.QDocumentNote;
import fi.finlit.edith.domain.QNote;

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
