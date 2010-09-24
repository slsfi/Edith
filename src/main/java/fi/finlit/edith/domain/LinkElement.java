/**
 *
 */
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public class LinkElement extends Identifiable implements ParagraphElement {

    @Predicate
    private String string;

    @Predicate
    private String reference;

    public LinkElement() {
    }

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