/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

/**
 * Note provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITH.NS)
public class Note extends Identifiable{
    
    @Predicate
    private Document document;

    // necessary ?!?
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
