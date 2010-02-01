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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

/**
 * DocumentRepositoryImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRepositoryImpl extends AbstractRepository<Document> implements
        DocumentRepository {

    private static final String XML_NS = "http://www.w3.org/XML/1998/namespace";

    private static final String TEI_NS = "http://www.tei-c.org/ns/1.0";

    private final String documentRoot;

    private final SubversionService svnService;

    private final NoteRepository noteRepository;

    private final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    private final XMLInputFactory inFactory = XMLInputFactory.newInstance();

    private final XMLOutputFactory outFactory = XMLOutputFactory.newInstance();

    public DocumentRepositoryImpl(
            @Inject SessionFactory sessionFactory,
            @Inject @Symbol(EDITH.SVN_DOCUMENT_ROOT) String documentRoot,
            @Inject SubversionService svnService,
            @Inject NoteRepository noteRepository)throws SVNException {
        super(sessionFactory, document);
        this.documentRoot = documentRoot;
        this.svnService = svnService;
        this.noteRepository = noteRepository;
    }

    @Override
    public void addDocument(String svnPath, File file) {
        svnService.importFile(svnPath, file);
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
    public InputStream getDocumentStream(DocumentRevision document) throws IOException {
        return svnService.getStream(document.getSvnPath(), document.getRevision());
    }

    @Override
    public Document getDocumentForPath(String svnPath) {
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
    public List<Long> getRevisions(Document document) {
        return svnService.getRevisions(document.getSvnPath());
    }

    @Override
    public void remove(Document document) {
        svnService.delete(document.getSvnPath());
    }

    @Override
    public Note addNote(Document doc, long revision, final String startId, final String endId,
            final String text) throws IOException {
        final String localId = UUID.randomUUID().toString();
        Long newRevision = svnService.commit(doc.getSvnPath(), revision, new UpdateCallback() {
            @Override
            public void update(InputStream source, OutputStream target) {
                try {
                    addNote(source, target, startId, endId, text, localId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // persisted noteRevision has svnRevision of newly created commit
        return noteRepository.createNote(doc, newRevision, localId, text, text);
    }

    public void addNote(InputStream source, OutputStream target, String startId, String endId,
            String text, String localId) throws Exception {
        AnchorPosition startPosition = new AnchorPosition(Assert.notNull(startId));
        AnchorPosition endPosition = new AnchorPosition(endId);
        System.err.println(startPosition + " - " + endPosition + " : " + text);

        XMLEventReader reader = inFactory.createXMLEventReader(source);
        XMLEventWriter writer = outFactory.createXMLEventWriter(target);

        try {
            int act = 0;
            int sp = 0;
            int stage = 0;
            boolean inSp = false;
            while (reader.hasNext()) {
                boolean handled = false;
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    StartElement element = event.asStartElement();
                    String localName = element.getName().getLocalPart();
                    if (localName.equals("div")) {
                        Iterator<Attribute> attributes = element.getAttributes();
                        boolean isAct = false;
                        int n = 0;
                        while (attributes.hasNext()) {
                            Attribute attr = attributes.next();
                            if (attr.getName().getLocalPart().equals("type") && attr.getValue().equals("act")) {
                                isAct = true;
                            } else if (attr.getName().getLocalPart().equals("n")) {
                                n = Integer.valueOf(attr.getValue());
                            }
                        }
                        if (isAct) {
                            act = n;
                            stage = 0;
                            sp = 0;
                        }
                    } else if (localName.equals("stage")) {
                        stage++;
                        inSp = false;

                    } else if (localName.equals("sp")) {
                        sp++;
                        inSp = true;
                    }
                } else if (event.isCharacters()) {
                    Characters characters = event.asCharacters();
                    String data = characters.getData();
                    int index = 0;

                    if (startPosition.matches(act, inSp ? sp : stage, inSp)) {
                        index = TextUtils.getStartIndex(data, text);
                        if (index > -1) {
                            // insert start anchor : start${localId}
                            if (index > 0) {
                                writer.add(eventFactory.createCharacters(data.substring(0, index)));
                            }
                            writer.add(eventFactory.createStartElement("", TEI_NS, "anchor"));
                            writer.add(eventFactory.createAttribute("xml", XML_NS, "id", "start" + localId));
                            writer.add(eventFactory.createEndElement("", TEI_NS, "anchor"));
                            handled = true;
                        }
                    }

                    if (endPosition.matches(act, inSp ? sp : stage, inSp)) {
                        int end = TextUtils.getEndIndex(data, text);
                        if (end > -1) {
                            if (end > index) {
                                writer.add(eventFactory
                                        .createCharacters(data.substring(index, end)));
                            }
                            writer.add(eventFactory.createStartElement("", TEI_NS, "anchor"));
                            writer.add(eventFactory.createAttribute("xml", XML_NS, "id", "end"
                                    + localId));
                            writer.add(eventFactory.createEndElement("", TEI_NS, "anchor"));
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

    @Override
    public void removeNoteAnchors(Document document, long svnRevision, final Note... notes)
            throws IOException {
        svnService.commit(document.getSvnPath(), svnRevision, new UpdateCallback() {
            @Override
            public void update(InputStream source, OutputStream target) {
                try {
                    removeNoteAnchors(source, target, notes);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // persisted noteRevision has svnRevision of newly created commit
        for (Note note : notes) {
            noteRepository.remove(note);
        }
    }

    public void removeNoteAnchors(InputStream source, OutputStream target, Note... notes) throws Exception {
        XMLEventReader reader = inFactory.createFilteredReader(inFactory.createXMLEventReader(source), createEventFilter(notes));
        XMLEventWriter writer = outFactory.createXMLEventWriter(target);

        try {
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                writer.add(event);
            }
        } finally {
            writer.close();
            reader.close();
        }
    }

    private static EventFilter createEventFilter(Note... notes) {
        final QName searchedAttributeName = new QName(XML_NS, "id");
        final Set<String> anchors = new HashSet<String>(notes.length * 2);

        for (Note note : notes) {
            anchors.add("start" + note.getLocalId());
            anchors.add("end" + note.getLocalId());
        }

        return new EventFilter() {
            private boolean remove = false;

            @Override
            public boolean accept(XMLEvent event) {
                if (event.isStartElement()) {
                    Attribute attr = event.asStartElement().getAttributeByName(
                            searchedAttributeName);
                    if (attr != null && anchors.contains(attr.getValue())) {
                        remove = true;
                        return false;
                    }
                } else if (event.isEndElement() && remove) {
                    remove = false;
                    return false;
                }

                return true;
            }
        };
    }

    @Override
    public void updateNode(Document document, NoteRevision note, String startId, String endId,
            String text) throws IOException {
        throw new UnsupportedOperationException();

    }
}
