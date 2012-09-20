/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.domain;

import java.util.ArrayList;
import java.util.List;

import com.mysema.edith.util.StringUtils;

public class Paragraph {
    private final List<ParagraphElement> elements = new ArrayList<ParagraphElement>();

    public List<ParagraphElement> getElements() {
        return elements;
    }

    public Paragraph addElement(ParagraphElement e) {
        elements.add(e);
        return this;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Paragraph) {
            return obj.toString().equals(toString());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return StringUtils.join(elements);
    }
}
