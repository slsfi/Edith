/**
 *
 */
package fi.finlit.edith.domain;


public class StringElement implements ParagraphElement {

    private String string;

    public StringElement() {
    }

    public StringElement(String string) {
        this.string = string;
    }

    @Override
    public ParagraphElement copy() {
        return new StringElement(string);
    }

    @Override
    public String toString() {
        return string;
    }
}