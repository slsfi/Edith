package fi.finlit.edith.ui.services;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.tapestry5.MarkupWriter;

/**
 * DocumentWriterImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRendererImpl implements DocumentRenderer {
    
    private static final String TEI_NS = "http://www.tei-c.org/ns/1.0";

    // sp -> speech, pb -> page break, lg -> line group, l -> line
    
    static final Set<String> emptyElements = new HashSet<String>(Arrays.asList("lb","pb"));
    
    static final Set<String> toDiv = new HashSet<String>(Arrays.asList(
            "title", "l","lg","publisher","pubPlace","sp","stage","teiHeader","text"));
    
    static final Set<String> toP = new HashSet<String>();
    
    static final Set<String> toSpan = new HashSet<String>(Arrays.asList(
            "actor","camera","caption","date","docAuthor","gap","orig","ref","role","roleDesc","set","sound","speaker","stage","tech","view"));
   
    static final Set<String> toUl = new HashSet<String>(Arrays.asList("castGroup","castList","listPerson"));
    
    static final Set<String> toLi = new HashSet<String>(Arrays.asList("castItem","person"));
    
    static final Set<String> toSelf = new HashSet<String>(Arrays.asList("div","p"));
    
    @Override
    public void render(File file, MarkupWriter writer) throws Exception{
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(file));
        
        while (true) {
            int event = reader.next();            
            if (event == XMLStreamConstants.START_ELEMENT){
                String localName = reader.getLocalName();
                if (toDiv.contains(localName)){
                    writer.element("div", "class", localName);
                }else if (toP.contains(localName)){
                    writer.element("p", "class", localName);
                }else if (toSpan.contains(localName)){
                    writer.element("span", "class", localName);
                }else if (toUl.contains(localName)){
                    writer.element("ul", "class", localName);
                }else if (toLi.contains(localName)){
                    writer.element("li", "class", localName);
                }else if (toSelf.contains(localName)){
                    writer.element(localName);
                }else if (localName.equals("TEI")){    
                    writer.element("div", "class", "tei");
                }else if (localName.equals("lb")){    
                    writer.element("br");
                    writer.end();
                }else if (localName.equals("pb")){
                    String page = reader.getAttributeValue(TEI_NS, "n");
                    if (page != null){
                        writer.element("div", "class", "page");
                        writer.writeRaw(page);
                        writer.end();    
                    }                    
                }else{
                    writer.element("div", "class", localName);
                }
                
            }else if (event == XMLStreamConstants.END_ELEMENT){
                String localName = reader.getLocalName();
                if (!emptyElements.contains(localName)){
                    writer.end();    
                }
                
            }else if (event == XMLStreamConstants.ATTRIBUTE){    
                // ?!?      
                
            }else if (event == XMLStreamConstants.CHARACTERS){
                writer.writeRaw(reader.getText());
                
            }else if (event == XMLStreamConstants.END_DOCUMENT) {
                reader.close();
                break;
            }
        }
    }

}
