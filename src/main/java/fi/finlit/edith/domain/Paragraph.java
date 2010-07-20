package fi.finlit.edith.domain;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringUtils;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Container;
import com.mysema.rdfbean.annotations.ContainerType;
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
        return StringUtils.join(elements, "");
    }

    public static Paragraph parseParagraph(String s) throws XMLStreamException {
        String document = new StringBuilder("<root>").append(s).append("</root>").toString();
        Paragraph paragraph = new Paragraph(new ArrayList<ParagraphElement>());
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(document));
        boolean inBib = false;
        String reference = null;
        while (true) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (reader.getLocalName().equals("bibliograph")) {
                    inBib = true;
                    if (reader.getAttributeCount() > 0) {
                        reference = reader.getAttributeValue(0);
                    }
                }
            } else if (event == XMLStreamConstants.CHARACTERS) {
                if (inBib) {
                    LinkElement element = new LinkElement(reader.getText());
                    if (reference != null) {
                        element.setReference(reference);
                    }
                    paragraph.addElement(element);
                } else {
                    paragraph.addElement(new StringElement(reader.getText()));
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                inBib = false;
                reference = null;
            } else if (event == XMLStreamConstants.END_DOCUMENT) {
                reader.close();
                break;
            }
        }
        return paragraph;
    }
}
