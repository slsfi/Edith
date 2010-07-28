/**
 *
 */
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public class StringElement extends Identifiable implements ParagraphElement {
    
    @Predicate
    private String string;

    public StringElement() {
    }

    public StringElement(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }
}