/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.domain;

public class LinkElement implements ParagraphElement {

    private final String string;

    private String reference;

    public LinkElement(String string) {
        this.string = string;
    }

    @Override
    public ParagraphElement copy() {
        LinkElement element = new LinkElement(string);
        element.setReference(reference);
        return element;
    }

    @Override
    public String toString() {
        return "<bibliograph" + (reference == null ? "" : " ref=\"" + reference + "\"") + ">"
                + string + "</bibliograph>";
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getReference() {
        return reference;
    }

    public String getString() {
        return string;
    }
}