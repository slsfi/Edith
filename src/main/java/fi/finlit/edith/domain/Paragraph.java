package fi.finlit.edith.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Container;
import com.mysema.rdfbean.annotations.ContainerType;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public class Paragraph extends Identifiable {

    @Container(ContainerType.LIST)
    @Predicate
    private final List<ParagraphElement> elements = new ArrayList<ParagraphElement>();

    public List<ParagraphElement> getElements() {
        return elements;
    }

    public void addElement(ParagraphElement e) {
        elements.add(e);
    }

    public Paragraph copy() {
        Paragraph paragraph = new Paragraph();
        for (ParagraphElement element : elements) {
            paragraph.addElement(element.copy());
        }
        return paragraph;
    }

    @Override
    public String toString() {
        return StringUtils.join(elements, "");
    }
}
