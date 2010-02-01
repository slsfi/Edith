/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ioc.annotations.InjectService;

import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;

/**
 * DocumentWriterImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRendererImpl implements DocumentRenderer {

    private static final String XML_NS = "http://www.w3.org/XML/1998/namespace";

    // sp -> speech, pb -> page break, lg -> line group, l -> line

    static final Set<String> emptyElements = new HashSet<String>(Arrays.asList("anchor", "lb", "pb"));

    static final Set<String> toDiv = new HashSet<String>(Arrays.asList(
            "title", "l","lg","publisher","pubPlace","teiHeader","text"));

    static final Set<String> toP = new HashSet<String>();

    static final Set<String> toSpan = new HashSet<String>(Arrays.asList(
            "actor","camera","caption","date","desc","docAuthor","gap","orig","ref","role","roleDesc","set","sound","speaker","tech","view"));

    static final Set<String> toUl = new HashSet<String>(Arrays.asList("castGroup","castList","listPerson"));

    static final Set<String> toLi = new HashSet<String>(Arrays.asList("castItem","person"));

    private final DocumentRepository documentRepo;
    
    private final XMLInputFactory inFactory = XMLInputFactory.newInstance();
    
    public DocumentRendererImpl(@InjectService("DocumentRepository") DocumentRepository documentRepo){
        this.documentRepo = documentRepo;
    }

    @Override
    public void renderPageLinks(DocumentRevision document, MarkupWriter writer) throws Exception{
        
        InputStream is = documentRepo.getDocumentStream(document);
        XMLStreamReader reader = inFactory.createXMLStreamReader(is);

        try{
            writer.element("ul", "class", "pages");
            while (true) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT){
                    String localName = reader.getLocalName();
                    if (localName.equals("pb")){
                        String page = reader.getAttributeValue(null, "n");
                        if (page != null){
                            writer.element("li");
                            writer.element("a", "href", "#page" + page);
                            writer.writeRaw(page);
                            writer.end();
                            writer.end();
                        }
                    }

                }else if (event == XMLStreamConstants.END_DOCUMENT) {                    
                    break;
                }
            }
            writer.end();
        }finally{
            reader.close();
            is.close();
        }

    }

    @Override
    public void renderDocument(DocumentRevision document, MarkupWriter writer) throws Exception{
        InputStream is = documentRepo.getDocumentStream(document);
        XMLStreamReader reader = inFactory.createXMLStreamReader(is);

        boolean noteContent = false;
        Set<String> noteIds = new HashSet<String>();

        int act = 0;
        int sp = 0;
        int stage = 0;

        try{
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
                    }else if (localName.equals("sp")){
                        sp++;
                        writer.element("div", "class", localName, "id", "act"+act+"-sp"+sp);
                    }else if (localName.equals("stage")){
                        stage++;
                        writer.element("div", "class", localName, "id", "act"+act+"-stage"+stage);
                    }else if (localName.equals("p")){
                        writer.element(localName);
                    }else if (localName.equals("div")){
                        String type = reader.getAttributeValue(null, "type");
                        if ("act".equals(type)){
                            act = Integer.valueOf(reader.getAttributeValue(null, "n"));
                            sp = 0;
                            stage = 0;
                            writer.element(localName, "class", "act", "id", "act" + act);
                        }else{
                            writer.element(localName);
                        }
                    }else if (localName.equals("TEI")){
                        writer.element("div", "class", "tei");
                    }else if (localName.equals("lb")){
                        writer.element("br");
                        writer.end();
                    }else if (localName.equals("pb")){
                        String page = reader.getAttributeValue(null, "n");
                        if (page != null){
                            writer.element("div", "id", "page" + page, "class", "page");
                            writer.writeRaw(page + ".");
                            writer.end();
                        }
                    }else if (localName.equals("anchor")){
                        String id = reader.getAttributeValue(XML_NS, "id");
                        if (id == null){
                            continue;
                        }else if (id.startsWith("start")){
                            // start anchor
                            writer.element("span", "class", "notestart", "id", id);
                            writer.end();

                            noteContent = true;
                            noteIds.add(id.substring("start".length()));
                        }else if (id.startsWith("end")){
                            noteIds.remove(id.substring("end".length()));
                            if (noteIds.isEmpty()){
                                noteContent = false;
                            }
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
                    String text = reader.getText();
                    if (noteContent && !text.trim().isEmpty()){
                        StringBuilder classes = new StringBuilder("notecontent");
                        for (String noteId : noteIds){
                            classes.append(" n").append(noteId);
                        }
                        writer.element("span", "class", classes);
                        writer.writeRaw(text);
                        writer.end();
                    }else{
                        writer.writeRaw(text);
                    }

                }else if (event == XMLStreamConstants.END_DOCUMENT) {                
                    break;
                }
            }
        }finally{
            reader.close();
            is.close();
        }
        

    }

}
