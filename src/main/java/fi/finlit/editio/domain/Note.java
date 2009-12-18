package fi.finlit.editio.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.editio.EDITIO;

/**
 * Note provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITIO.NS)
public class Note {
    
    @Predicate
    private Document document;

    @Predicate
    private NoteRevision latestRevision;
    
    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public NoteRevision getLatestRevision() {
        return latestRevision;
    }

    public void setLatestRevision(NoteRevision latestRevision) {
        this.latestRevision = latestRevision;
    }
        
}
