/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;

import fi.finlit.edith.EDITH;

// TODO Rename according to Java naming conventions (CamelCase -> ALL_CAPS).
/**
 * NoteStatus provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns = EDITH.NS)
public enum NoteStatus {
    /**
     *
     */
    Draft,
    /**
     *
     */
    Finished,
    /**
     *
     */
    LockedForEdit,
    /**
     *
     */
    Publishable,
    /**
     *
     */
    Initial;
}
