package fi.finlit.edith.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Container;
import com.mysema.rdfbean.annotations.ContainerType;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.ui.services.ParagraphParser;

@ClassMapping(ns = EDITH.NS)
public class Paragraph extends Identifiable {

    public static final Paragraph parseSafe(String s){
        if (s != null){
            try {
                return ParagraphParser.parseParagraph(s);
            } catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
        }else{
            return null;
        }
    }

    @Container(ContainerType.LIST)
    @Predicate
    private final List<ParagraphElement> elements = new ArrayList<ParagraphElement>();

    public List<ParagraphElement> getElements() {
        return elements;
    }

    public Paragraph addElement(ParagraphElement e) {
        elements.add(e);
        return this;
    }

    public Paragraph copy() {
        Paragraph paragraph = new Paragraph();
        for (ParagraphElement element : elements) {
            paragraph.addElement(element.copy());
        }
        return paragraph;
    }

    @Override
    public int hashCode(){
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }else if (obj instanceof Paragraph){
            return obj.toString().equals(toString());
        }else{
            return false;
        }
    }

    @Override
    public String toString() {
        return StringUtils.join(elements, "");
    }
}
