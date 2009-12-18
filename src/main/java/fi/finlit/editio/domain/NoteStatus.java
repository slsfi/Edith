package fi.finlit.editio.domain;

import com.mysema.rdfbean.annotations.ClassMapping;

import fi.finlit.editio.EDITIO;

/**
 * NoteStatus provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITIO.NS)
public enum NoteStatus {
    /**
     * 
     */
    DRAFT,
    /**
     * 
     */
    LOCKED_FOR_EDIT,
    /**
     * 
     */
    FINISHED,
    /**
     * 
     */
    PUBLISHABLE;
}
