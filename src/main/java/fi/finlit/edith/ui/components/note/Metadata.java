/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.components.note;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import fi.finlit.edith.sql.domain.Note;

@SuppressWarnings("unused")
public class Metadata {
    @Property
    @Parameter(required = true)
    private Note note;
}
