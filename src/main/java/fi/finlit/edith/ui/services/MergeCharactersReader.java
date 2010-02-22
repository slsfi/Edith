package fi.finlit.edith.ui.services;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * MergedCharactersReader provides
 *
 * @author tiwe
 * @version $Id$
 */
public class MergeCharactersReader implements XMLEventReader{
    
    private final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    private final XMLEventReader reader;
    
    public MergeCharactersReader(XMLEventReader reader) {
        this.reader = reader;
    }

    public void close() throws XMLStreamException {
        reader.close();
    }

    public String getElementText() throws XMLStreamException {
        return reader.getElementText();
    }

    public Object getProperty(String arg0) throws IllegalArgumentException {
        return reader.getProperty(arg0);
    }

    public boolean hasNext() {
        return reader.hasNext();
    }

    public Object next() {
        try {
            return nextEvent();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public XMLEvent nextEvent() throws XMLStreamException {
        XMLEvent event = reader.nextEvent();
        if (event.isCharacters()){
            StringBuilder builder = new StringBuilder(event.asCharacters().getData());
            while (peek() != null && peek().isCharacters()){
                builder.append(nextEvent().asCharacters().getData());
            }
            return eventFactory.createCharacters(builder.toString());
            
        }else{
            return event;
        }
    }

    public XMLEvent nextTag() throws XMLStreamException {
        return reader.nextTag();
    }

    public XMLEvent peek() throws XMLStreamException {
        return reader.peek();
    }

    public void remove() {
        reader.remove();
    }

}
