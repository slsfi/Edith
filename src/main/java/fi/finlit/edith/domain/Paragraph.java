package fi.finlit.edith.domain;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public class Paragraph extends Identifiable {
    @Predicate
    private List<ParagraphElement> elements;

    public Paragraph() {
    }

    public Paragraph(List<ParagraphElement> elements) {
        this.elements = elements;
    }

    public void addElement(ParagraphElement e) {
        elements.add(e);
    }

    @Override
    public String toString() {
        // FIXME This doesn't seem very flexible.
        return StringUtils.join(elements, " ").trim();
    }
}
