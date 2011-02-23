/**
 *
 */
package fi.finlit.edith.domain;


public class LinkElement extends Identifiable implements ParagraphElement {

    private String string;

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