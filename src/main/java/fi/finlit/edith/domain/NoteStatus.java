/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;

@ClassMapping
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
