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

    public void addNote(XMLEventReader reader, XMLEventWriter writer, SelectedText selection, String localId) throws Exception {
        String startId = selection.getStartId();
        String endId = selection.getEndId();
        System.err.println(startId + " - " + endId + " : " + selection.getSelection());

        ElementContext context = new ElementContext(3);
        
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
                    Characters characters = event.asCharacters();
                    String data = characters.getData();
                    int index = 0;

                    if (startId.equals(context.getPath())){                        
                        index = TextUtils.getStartIndex(data, selection.getSelection());
                        if (index > -1) {
                            // insert start anchor : start${localId}
                            if (index > 0) {
                                writer.add(eventFactory.createCharacters(data.substring(0, index)));
                            }
                            writer.add(eventFactory.createStartElement("", TEI_NS, "anchor"));
                            writer.add(eventFactory.createAttribute("xml", XML_NS, "id", "start" + localId));
                            writer.add(eventFactory.createEndElement("", TEI_NS, "anchor"));
                            logger.info("start"+localId +" added");
                            handled = true;
                        } else {
                            index = 0;
                        }
                    }

                    if (endId.equals(context.getPath())){
                        int end = TextUtils.getEndIndex(data, selection.getSelection());
                        if (end > -1) {
                            if (end > index) {
                                writer.add(eventFactory.createCharacters(data.substring(index, end)));
                            }
                            writer.add(eventFactory.createStartElement("", TEI_NS, "anchor"));
                            writer.add(eventFactory.createAttribute("xml", XML_NS, "id", "end" + localId));
                            writer.add(eventFactory.createEndElement("", TEI_NS, "anchor"));
                            logger.info("end"+localId +" added");
                            index = end;
                            handled = true;
                        }
                    }

                    if (handled && index < data.length() - 1) {
                        writer.add(eventFactory.createCharacters(data.substring(index)));
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

}
