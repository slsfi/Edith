package fi.finlit.edith.sql.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;

import fi.finlit.edith.util.ParagraphParser;

public class Paragraph {
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
