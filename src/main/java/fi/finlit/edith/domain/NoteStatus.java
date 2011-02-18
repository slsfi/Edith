/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public enum NoteStatus {
    /**
     *
     */
    INITIAL,

    /**
     *
     */
    DRAFT,

    /**
     *
     */
    FINISHED;
}
