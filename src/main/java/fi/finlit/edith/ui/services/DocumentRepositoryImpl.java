/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QDocument.document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import com.mysema.commons.lang.Assert;
import com.mysema.rdfbean.dao.AbstractRepository;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;
import fi.finlit.edith.domain.SelectedText;

/**
 * DocumentRepositoryImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRepositoryImpl extends AbstractRepository<Document> implements
        DocumentRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentRepositoryImpl.class);
    
    private static final String TEI_NS = "http://www.tei-c.org/ns/1.0";

    private static final String XML_NS = "http://www.w3.org/XML/1998/namespace";
    
    private static final QName TEI_TYPE_QNAME = new QName(null, "type");
    
    private static final QName XML_ID_QNAME = new QName(XML_NS, "id");
    
    private final String documentRoot;

    private final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    private final XMLInputFactory inFactory = XMLInputFactory.newInstance();

    private final NoteRepository noteRepository;

    private final NoteRevisionRepository noteRevisionRepository;

    private final XMLOutputFactory outFactory = XMLOutputFactory.newInstance();

    private final SubversionService svnService;
    
    private final TimeService timeService;
    
    public DocumentRepositoryImpl(
            @Inject SessionFactory sessionFactory,
            @Inject @Symbol(EDITH.SVN_DOCUMENT_ROOT) String documentRoot,
            @Inject SubversionService svnService,
            @Inject NoteRepository noteRepository,
            @Inject NoteRevisionRepository noteRevisionRepository,
            @Inject TimeService timeService)throws SVNException {
        super(sessionFactory, document);
        this.documentRoot = documentRoot;
        this.svnService = svnService;
        this.noteRepository = noteRepository;
        this.noteRevisionRepository = noteRevisionRepository;
        this.timeService = timeService;
    }

    private static EventFilter createRemoveFilter(Note... notes) {
        final Set<String> anchors = new HashSet<String>(notes.length * 2);

        for (Note note : notes) {
            anchors.add("start" + note.getLocalId());
            anchors.add("end" + note.getLocalId());
        }

        return new EventFilter() {
            private boolean removeNextEndElement = false;

            @Override
            public boolean accept(XMLEvent event) {
                if (event.isStartElement()) {
                    Attribute attr = event.asStartElement().getAttributeByName(XML_ID_QNAME);
                    if (attr != null && anchors.contains(attr.getValue())) {
                        removeNextEndElement = true;
                        return false;
                    }
                } else if (event.isEndElement() && removeNextEndElement) {
                    removeNextEndElement = false;
                    return false;
                }

                return true;
            }
        };
    }

    @Override
    public void addDocument(String svnPath, File file) {
        svnService.importFile(svnPath, file);
    }

    @Override
    public NoteRevision addNote(DocumentRevision docRevision, final SelectedText selection) throws IOException{
        final String localId = String.valueOf(timeService.currentTimeMillis());
        Long newRevision = svnService.commit(docRevision.getSvnPath(), docRevision.getRevision(), new UpdateCallback() {
            @Override
            public void update(InputStream source, OutputStream target) {
                try {
                    addNote(inFactory.createXMLEventReader(source), outFactory
                            .createXMLEventWriter(target), selection, localId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        docRevision = new DocumentRevision(docRevision, newRevision);
        // persisted noteRevision has svnRevision of newly created commit
        return noteRepository.createNote(docRevision, localId,selection.getSelection()).getLatestRevision();
    }

    public void addNote(XMLEventReader reader, XMLEventWriter writer, SelectedText sel, String localId) throws Exception {
        logger.info(sel.getStartId() + " - " + sel.getEndId() + " : " + sel.getSelection());        
        ElementContext context = new ElementContext(3);
        StringBuilder startBuilder = new StringBuilder();
        StringBuilder endBuilder = new StringBuilder();
        boolean startMatched = false, endMatched = false;
        
        try {
            while (reader.hasNext()) {
                boolean handled = false;
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    StartElement element = event.asStartElement();
                    String localName = element.getName().getLocalPart();
                    String name = localName;
                    if (localName.equals("div")){
                        name = element.getAttributeByName(TEI_TYPE_QNAME).getValue();
                    }
                    context.push(name);
                                        
                } else if (event.isEndElement()){
                    context.pop();
                    
                } else if (event.isCharacters()) {
                    Characters chars = event.asCharacters();
                    int index = 0;
                    
                    // in start
                    if (sel.getStartId().equals(context.getPath())){
                        startBuilder.append(chars.getData());
                        String str = startBuilder.toString();                        
                        
                        // found first word
                        if (str.contains(sel.getFirstWord()) && !startMatched){
                            index = getIndex(str, sel.getFirstWord(), sel.getStartIndex());
                            if (index >= 0){
                                startMatched = true;
                                handled = true;
                                index = index - str.length() + chars.getData().length();
                                if (index > 0){
                                    writer.add(eventFactory.createCharacters(chars.getData().substring(0, index)));
                                }
                                writeAnchor(writer, "start"+localId);
                            }else{
                                index = 0;
                            }
                        }                        
                    }
                    
                    // in end
                    if (sel.getEndId().equals(context.getPath())){
                        String str;
                        if (sel.getStartId().equals(sel.getEndId())){
                            str = startBuilder.toString();
                        }else{
                            str = endBuilder.append(chars.getData()).toString();
                        }
                        if (str.contains(sel.getLastWord()) && !endMatched){
                            int endIndex = getIndex(str, sel.getLastWord(), sel.getEndIndex());
                            
                            // found last word
                            if (endIndex >= 0){
                                endMatched = true;
                                handled = true;
                                endIndex = endIndex - str.length() + chars.getData().length() + sel.getLastWord().length();
                                writer.add(eventFactory.createCharacters(chars.getData().substring(index, endIndex)));
                                writeAnchor(writer, "end"+localId);
                                index = endIndex;
                            }                            
                        }
                    }
                    
                    // stream rest as characters
                    if (handled && index < chars.getData().length()){
                        writer.add(eventFactory.createCharacters(chars.getData().substring(index)));
                    }

                }
                if (!handled) {
                    writer.add(event);
                }
            }
        } finally {
            writer.close();
            reader.close();
        }
    }
    
    public static int getIndex(String str, String word, int occurrence) {
        int index = -1;
        while (occurrence > 0){
            index = str.indexOf(word, index+1);
            occurrence--;
        }
        return index;
        
    }

    private Document createDocument(String path, String title, String description) {
        Document document = new Document();
        document.setSvnPath(path);
        document.setTitle(title);
        document.setDescription(description);
        return save(document);
    }
    
    @Override
    public Collection<Document> getAll() {
        return getDocumentsOfFolder(documentRoot);
    }

    @Override
    public Document getDocumentForPath(String svnPath) {
        Assert.notNull(svnPath, "svnPath was null");
        Document document = getDocumentMetadata(svnPath);
        if (document == null) {
            document = createDocument(svnPath, svnPath.substring(svnPath.lastIndexOf('/') + 1), null);
        }
        return document;
    }

    private Document getDocumentMetadata(String svnPath) {
        return getSession().from(document)
            .where(document.svnPath.eq(svnPath))
            .uniqueResult(document);
    }

    @Override
    public List<Document> getDocumentsOfFolder(String svnFolder) {
        Assert.notNull(svnFolder, "svnFolder was null");
        Collection<String> entries = svnService.getEntries(svnFolder, /* HEAD */-1);
        List<Document> documents = new ArrayList<Document>(entries.size());
        for (String entry : entries) {
            String path = svnFolder + "/" + entry;
            Document document = getDocumentMetadata(path);
            if (document == null) {
                document = createDocument(path, entry, null);
            }
            documents.add(document);
        }
        return documents;
    }

    @Override
    public InputStream getDocumentStream(DocumentRevision document) throws IOException {
        Assert.notNull(document, "document was null");
        return svnService.getStream(document.getSvnPath(), document.getRevision());
    }

    @Override
    public List<Long> getRevisions(Document document) {
        Assert.notNull(document, "document was null");
        return svnService.getRevisions(document.getSvnPath());
    }

    @Override
    public void remove(Document document) {
        Assert.notNull(document, "document was null");
        svnService.delete(document.getSvnPath());
    }

    @Override
    public DocumentRevision removeNotes(DocumentRevision docRevision, final Note... notes)throws IOException {
        long newRevision = svnService.commit(docRevision.getSvnPath(), docRevision.getRevision(), new UpdateCallback() {
            @Override
            public void update(InputStream source, OutputStream target) {
                try {
                    streamEvents(inFactory.createFilteredReader(inFactory
                            .createXMLEventReader(source), createRemoveFilter(notes)), outFactory
                            .createXMLEventWriter(target));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // persisted noteRevision has svnRevision of newly created commit
        for (Note note : notes) {
            noteRepository.remove(note, newRevision);
        }
        
        return new DocumentRevision(docRevision, newRevision);
    }

    public void streamEvents(XMLEventReader reader, XMLEventWriter writer) throws Exception {
        try {
            while (reader.hasNext()) {
                writer.add(reader.nextEvent());
            }
        } finally {
            writer.close();
            reader.close();
        }
    }

    @Override
    public NoteRevision updateNote(final NoteRevision note, final SelectedText selection) throws IOException {
        Document document = note.getRevisionOf().getDocument();
        long newRevision = svnService.commit(document.getSvnPath(), note.getSvnRevision(),
                new UpdateCallback() {
                    @Override
                    public void update(InputStream source, OutputStream target) {
                        try {
                            addNote(inFactory.createFilteredReader(inFactory
                                    .createXMLEventReader(source),
                                    createRemoveFilter(new Note[] { note.getRevisionOf() })),
                                    outFactory.createXMLEventWriter(target), selection,
                                    note.getRevisionOf().getLocalId());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
        });

        NoteRevision copy = note.createCopy();
        copy.setLongText(selection.getSelection());
        copy.setSVNRevision(newRevision);
        noteRevisionRepository.save(copy);
        return copy;
    }

    private void writeAnchor(XMLEventWriter writer, String anchorId) throws XMLStreamException{
        writer.add(eventFactory.createStartElement("", TEI_NS, "anchor"));
        writer.add(eventFactory.createAttribute("xml", XML_NS, "id", anchorId));
        writer.add(eventFactory.createEndElement("", TEI_NS, "anchor"));
    }

    @Override
    public DocumentRevision removeAllNotes(Document document) throws IOException {
        try {
            long revision = svnService.getLatestRevision(document.getSvnPath());
            DocumentRevision docRevision = document.getRevision(revision);
            List<NoteRevision> noteRevisions = noteRevisionRepository.getOfDocument(docRevision);
            Note[] notes = new Note[noteRevisions.size()];
            for (int i = 0; i < notes.length; i++){
                notes[i] = noteRevisions.get(i).getRevisionOf();
            }
            return removeNotes(docRevision, notes);
        } catch (SVNException e) {
            throw new IOException(e);
        }
        
    }

}
