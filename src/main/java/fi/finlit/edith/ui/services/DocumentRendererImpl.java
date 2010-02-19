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
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ioc.annotations.Inject;

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

    static final Set<String> EMPTY_ELEMENTS = new HashSet<String>(Arrays.asList("anchor", "lb", "pb"));

    static final Set<String> UL_ELEMENTS = new HashSet<String>(Arrays.asList("castGroup","castList","listPerson"));

    static final Set<String> LI_ELEMENTS = new HashSet<String>(Arrays.asList("castItem","person"));

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private final DocumentRepository documentRepo;

    private final XMLInputFactory inFactory = XMLInputFactory.newInstance();

    public DocumentRendererImpl(@Inject DocumentRepository documentRepo){
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
        ElementContext context = new ElementContext(3);

        try{
            while (true) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT){
                    String localName = reader.getLocalName();
                    String name = localName;
                    if (localName.equals("div")){
                        name = reader.getAttributeValue(null, "type");
                    }
                    context.push(name);
                    String path = context.getPath();

                    if (UL_ELEMENTS.contains(localName)){
                        writer.element("ul", "class", localName);
                        if (path != null) {
                            writer.attributes("id", path);
                        }

                    }else if (LI_ELEMENTS.contains(localName)){
                        writer.element("li", "class", localName);
                        if (path != null) {
                            writer.attributes("id", path);
                        }

                    }else if (localName.equals("div")){
                        String type = reader.getAttributeValue(null, "type");
                        writer.element(localName, "class", type);
                        if (path != null) {
                            writer.attributes("id", path);
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
                        writer.element("div", "class", name);
                        if (path != null) {
                            writer.attributes("id", path);
                        }
                    }

                }else if (event == XMLStreamConstants.END_ELEMENT){
                    context.pop();

                    String localName = reader.getLocalName();
                    if (!EMPTY_ELEMENTS.contains(localName)){
                        writer.end();
                    }

                }else if (event == XMLStreamConstants.CHARACTERS){
                    String text = WHITESPACE.matcher(reader.getText()).replaceAll(" ");
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
