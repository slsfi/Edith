/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.components.note;

import org.hibernate.annotations.Parameter;

import com.mysema.edith.domain.Note;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@SuppressWarnings("unused")
public class Metadata {
    @Property
    @Parameter(required = true)
    private Note note;
}
