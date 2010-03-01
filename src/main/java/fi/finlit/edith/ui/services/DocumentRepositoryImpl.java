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
import fi.finlit.edith.ui.services.svn.RevisionInfo;
import fi.finlit.edith.ui.services.svn.SubversionService;
import fi.finlit.edith.ui.services.svn.UpdateCallback;

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

    private final AuthService authService;

    public DocumentRepositoryImpl(
            @Inject SessionFactory sessionFactory,
            @Inject @Symbol(EDITH.SVN_DOCUMENT_ROOT) String documentRoot,
            @Inject SubversionService svnService,
            @Inject NoteRepository noteRepository,
            @Inject NoteRevisionRepository noteRevisionRepository,
            @Inject TimeService timeService,
            @Inject AuthService authService)throws SVNException {
        super(sessionFactory, document);
        this.documentRoot = documentRoot;
        this.svnService = svnService;
        this.noteRepository = noteRepository;
        this.noteRevisionRepository = noteRevisionRepository;
        this.timeService = timeService;
        this.authService = authService;
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

    public static int getIndex(String str, String word, int occurrence) {
        int index = -1;
        while (occurrence > 0){
            index = str.indexOf(word, index+1);
            if (index == -1) {
                return -1;
            }
            occurrence--;
        }
        return index;
    }

    @Override
    public void addDocument(String svnPath, File file) {
        svnService.importFile(svnPath, file);
    }

    @Override
    public NoteRevision addNote(DocumentRevision docRevision, final SelectedText selection) throws IOException, NoteAdditionFailedException{
        final String localId = String.valueOf(timeService.currentTimeMillis());
        long newRevision;
        try {
            newRevision = svnService.commit(docRevision.getSvnPath(), docRevision.getRevision(), authService.getUsername(),
                    new UpdateCallback() {
                @Override
                public void update(InputStream source, OutputStream target) throws Exception {
                    addNote(inFactory.createXMLEventReader(source), outFactory
                            .createXMLEventWriter(target), selection, localId);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        docRevision = new DocumentRevision(docRevision, newRevision);
        // persisted noteRevision has svnRevision of newly created commit
        return noteRepository.createNote(docRevision, localId,selection.getSelection()).getLatestRevision();
    }

    public void addNote(XMLEventReader reader, XMLEventWriter writer, SelectedText sel, String localId) throws Exception {
        logger.info(sel.toString());
        ElementContext context = new ElementContext(3);
        StringBuilder builder = new StringBuilder();
        List<XMLEvent> events = new ArrayList<XMLEvent>();
        String startAnchor = "start"+localId;
        String endAnchor = "end"+localId;

        boolean startMatched = false;
        boolean endMatched = false;
        try {
            boolean buffering = false;
            boolean startAndEndInSameElement = sel.getStartId().equals(sel.getEndId());
            while (reader.hasNext()) {
                boolean handled = false;
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    context.push(extractName(event.asStartElement()));
                    if (buffering) {
                        events.add(event);
                        handled = true;
                    }
                    if (isInContext(event, context, sel)){
                        buffering = true;
                    }
                } else if (event.isCharacters()) {
                    if (buffering) {
                        events.add(event);
                        handled = true;
                        if (isInContext(event, context, sel)){
                            builder.append(event.asCharacters().getData());
                        }
                    }
                } else if (event.isEndElement()) {
                    /*
                     * TODO Handle also cases where start context is in end context and end in start context.
                     * - in start context when end context begins flush start
                     * - in end context when start context begins and ends
                     */
                    if (isInContext(event, context, sel)) {
                        int offset = 0;
                        int startIndex = getIndex(builder.toString(), sel.getFirstWord(), sel.getStartIndex());
                        int endIndex = getIndex(builder.toString(), sel.getLastWord(), sel.getEndIndex()) + sel.getLastWord().length();
                        for (XMLEvent e : events) {
                            boolean innerHandled = false;
                            if (e.isStartElement()) {
                                context.push(extractName(e.asStartElement()));
                            } else if (e.isEndElement()) {
                                context.pop();
                            } else if (e.isCharacters() && isInContext(event, context, sel)) {
                                String eventString = e.asCharacters().getData();
                                int relativeStart = startIndex - offset;
                                int relativeEnd = endIndex - offset;
                                int index = -1;
                                offset += eventString.length();
                                if (sel.getStartId().equals(context.getPath())) {
                                    if (!startMatched && startIndex <= offset) {
                                        writer.add(eventFactory.createCharacters(eventString.substring(0, relativeStart)));
                                        writeAnchor(writer, startAnchor);
                                        startMatched = true;
                                        innerHandled = true;
                                        index = relativeStart;
                                    }
                                }
                                if (sel.getEndId().equals(context.getPath())) {
                                    if (!endMatched && endIndex <= offset) {
                                        if (!startAndEndInSameElement) {
                                            writer.add(eventFactory.createCharacters(eventString.substring(0, relativeEnd)));
                                        } else {
                                            writer.add(eventFactory.createCharacters(eventString.substring(relativeStart > -1 ? relativeStart : 0, relativeEnd)));
                                        }
                                        writeAnchor(writer, endAnchor);
                                        endMatched = true;
                                        innerHandled = true;
                                        index = relativeEnd;
                                    }
                                }
                                if (innerHandled) {
                                    writer.add(eventFactory.createCharacters(eventString.substring(index)));
                                }
                            }
                            if (!innerHandled) {
                                writer.add(e);
                            }
                        }
                        buffering = false;
                        events.clear();
                        builder = new StringBuilder();
                    }
                    context.pop();
                    if (buffering) {
                        buffering = isInContext(event, context, sel);
                        events.add(event);
                        handled = true;
                    }
                }
                if (!handled) {
                    writer.add(event);
                }
            }
        } finally {
            if (!startMatched || !endMatched) {
                throw new NoteAdditionFailedException(sel, localId, startMatched, endMatched);
            }
            writer.close();
            reader.close();
        }
    }

    /*
     * Evaluates if we are in an element that should be buffered
     */
    // TODO : find better name
    private boolean isInContext(XMLEvent event, ElementContext context, SelectedText sel) {
        return sel.getStartId().equals(context.getPath()) || sel.getEndId().equals(context.getPath());
    }

    private String extractName(StartElement element) {
        String localName = element.getName().getLocalPart();
        String name = localName;
        if (localName.equals("div")){
            name = element.getAttributeByName(TEI_TYPE_QNAME).getValue();
        }
        return name;
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
    public List<RevisionInfo> getRevisions(Document document) {
        Assert.notNull(document, "document was null");
        return svnService.getRevisions(document.getSvnPath());
    }

    @Override
    public void remove(Document document) {
        Assert.notNull(document, "document was null");
        svnService.delete(document.getSvnPath());
    }

    @Override
    public DocumentRevision removeAllNotes(Document document) {
        long revision = svnService.getLatestRevision(document.getSvnPath());
        DocumentRevision docRevision = document.getRevision(revision);
        List<NoteRevision> noteRevisions = noteRevisionRepository.getOfDocument(docRevision);
        Note[] notes = new Note[noteRevisions.size()];
        for (int i = 0; i < notes.length; i++){
            notes[i] = noteRevisions.get(i).getRevisionOf();
        }
        return removeNotes(docRevision, notes);
    }

    @Override
    public DocumentRevision removeNotes(DocumentRevision docRevision, final Note... notes){
        long newRevision;
        try {
            newRevision = svnService.commit(docRevision.getSvnPath(), docRevision.getRevision(), authService.getUsername(),
                    new UpdateCallback() {
                @Override
                public void update(InputStream source, OutputStream target) throws Exception {
                    streamEvents(inFactory.createFilteredReader(
                            inFactory.createXMLEventReader(source),
                            createRemoveFilter(notes)),
                            outFactory.createXMLEventWriter(target));
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
        long newRevision;
        try {
            newRevision = svnService.commit(document.getSvnPath(), note.getSvnRevision(), authService.getUsername(),
                    new UpdateCallback() {
                        @Override
                        public void update(InputStream source, OutputStream target) {
                            try {
                                XMLEventReader eventReader = inFactory.createFilteredReader(
                                        inFactory.createXMLEventReader(source),
                                        createRemoveFilter(new Note[] { note.getRevisionOf() }));
                                addNote(eventReader,
                                        outFactory.createXMLEventWriter(target),
                                        selection, note.getRevisionOf().getLocalId());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

}
