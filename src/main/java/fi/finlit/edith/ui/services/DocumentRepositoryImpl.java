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

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysema.commons.lang.Assert;
import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.QDocumentNote;
import fi.finlit.edith.domain.SelectedText;
import fi.finlit.edith.ui.services.svn.RevisionInfo;
import fi.finlit.edith.ui.services.svn.SubversionService;
import fi.finlit.edith.ui.services.svn.UpdateCallback;

public class DocumentRepositoryImpl extends AbstractRepository<Document> implements
        DocumentRepository {

    private static class Matched {

        private boolean startMatched;
        private boolean endMatched;

        public boolean areBothMatched() {
            return startMatched && endMatched;
        }
        public boolean isEndMatched() {
            return endMatched;
        }
        public boolean isStartMatched() {
            return startMatched;
        }
        public void matchEnd() {
            endMatched = true;
        }

        public void matchStart() {
            startMatched = true;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(DocumentRepositoryImpl.class);

    private static final String TEI_NS = "http://www.tei-c.org/ns/1.0";

    private static final String XML_NS = "http://www.w3.org/XML/1998/namespace";

    private static final QName TEI_TYPE_QNAME = new QName(null, "type");

    private static final QName XML_ID_QNAME = new QName(XML_NS, "id");

    private static EventFilter createRemoveFilter(DocumentNote... notes) {
        final Set<String> anchors = new HashSet<String>(notes.length * 2);

        for (DocumentNote note : notes) {
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

    public static String extractName(StartElement element) {
        String localName = element.getName().getLocalPart();
        String name = localName;
        if (localName.equals("div")){
            Attribute attribute = element.getAttributeByName(TEI_TYPE_QNAME);
            if (attribute != null){
                name = attribute.getValue();
            }
        }
        return name;
    }

    public static int getIndex(String str, String word, int occurrence) {
        int index = -1;
        int n = occurrence;
        while (n > 0){
            index = str.indexOf(word, index+1);
            if (index == -1) {
                return -1;
            }
            n--;
        }
        return index;
    }

    private final String documentRoot;

    private final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    private final XMLInputFactory inFactory = XMLInputFactory.newInstance();

    private final NoteRepository noteRepository;

    private final DocumentNoteRepository documentNoteRepository;

    private final XMLOutputFactory outFactory = XMLOutputFactory.newInstance();

    private final SubversionService svnService;

    private final TimeService timeService;

    private final AuthService authService;
            
    public DocumentRepositoryImpl(
            @Inject SessionFactory sessionFactory,
            @Inject @Symbol(EDITH.SVN_DOCUMENT_ROOT) String documentRoot,
            @Inject SubversionService svnService,
            @Inject NoteRepository noteRepository,
            @Inject DocumentNoteRepository documentNoteRepository,
            @Inject TimeService timeService,
            @Inject AuthService authService) {
        super(sessionFactory, document);
        this.documentRoot = documentRoot;
        this.svnService = svnService;
        this.noteRepository = noteRepository;
        this.documentNoteRepository = documentNoteRepository;
        this.timeService = timeService;
        this.authService = authService;
    }

    @Override
    public void addDocument(String svnPath, File file) {
        svnService.importFile(svnPath, file);
    }

    @Override
    public DocumentNote addNote(Note note, DocumentRevision docRevision, final SelectedText selection) throws IOException, NoteAdditionFailedException{
        final String localId = String.valueOf(timeService.currentTimeMillis());
        long newRevision;
        newRevision = svnService.commit(docRevision.getSvnPath(), docRevision.getRevision(),
                authService.getUsername(), new UpdateCallback() {
                    @Override
                    public void update(InputStream source, OutputStream target) throws IOException {
                        try {
                            addNote(inFactory.createXMLEventReader(source), outFactory
                                    .createXMLEventWriter(target), selection, localId);
                        } catch (XMLStreamException e) {
                            throw new ServiceException(e);
                        } catch (NoteAdditionFailedException e) {
                            throw new ServiceException(e);
                        }
                    }
                });

        // persisted noteRevision has svnRevision of newly created commit
        return noteRepository.createDocumentNote(note, new DocumentRevision(docRevision, newRevision), localId,selection.getSelection());
    }

    public void addNote(XMLEventReader reader, XMLEventWriter writer, SelectedText sel, String localId) throws NoteAdditionFailedException {
        logger.info(sel.toString());
        ElementContext context = new ElementContext(3);
        /* Used to concat all the strings while buffering. */
        StringBuilder allStrings = new StringBuilder();
        /* Used to concat all end context strings while buffering. */
        StringBuilder startStrings = new StringBuilder();
        /* Used to concat all start context strings while buffering. */
        StringBuilder endStrings = new StringBuilder();
        /* Used to store all the events while buffering. */
        List<XMLEvent> events = new ArrayList<XMLEvent>();
        MutableInt endOffset = new MutableInt(0);

        Matched matched = new Matched();
        try {
            boolean buffering = false;
            /* This boolean is used as a flag to signal if the buffering has been started at some point. This is
             * because we don't want to buffer the whole document which would happen in isEndElement else if.
             * FIXME I find this a bit ugly. */
            boolean startedBuffering = false;
            while (reader.hasNext()) {
                boolean handled = false;
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    context.push(extractName(event.asStartElement()));
                    if (buffering && !matched.areBothMatched()) {
                        handled = true;
                        if (context.equalsAny(sel.getStartId())) {
                            /* If the end element is inside the start element, we want to flush the end elements that do not
                             * contain the desired end anchor position. */
                            ElementContext tempContext = (ElementContext) context.clone();
                            /* tempContext is used so that we can send the actual context in most of these use cases.
                             * The first pop is always mandatory, the following ones only if we are even deeper in. */
                            tempContext.pop();
                            if (sel.isStartChildOfEnd()) {
                                for (int i = 1; i < sel.howDeepIsStartInEnd(); ++i) {
                                    tempContext.pop();
                                }
                            }
                            flush(writer, endStrings.toString(), sel, events, tempContext, matched, localId, endOffset);
                            allStrings = new StringBuilder();
                            events.clear();
                            handled = false;
                        } else if (context.equalsAny(sel.getEndId())) {
                            /* If the start element is inside the end element, we want to flush the start elements once
                             * reaching the element containing the end anchor. */
                            ElementContext tempContext = (ElementContext) context.clone();
                            tempContext.pop();
                            if (sel.isEndChildOfStart()) {
                                for (int i = 1; i < sel.howDeepIsEndInStart(); ++i) {
                                    tempContext.pop();
                                }
                            }
                            flush(writer, startStrings.toString(), sel, events, tempContext, matched, localId, endOffset);
                            allStrings = new StringBuilder();
                            events.clear();
                            handled = false;
                        } else {
                            events.add(event);
                        }
                    }
                    if (context.equalsAny(sel.getStartId(), sel.getEndId()) && !matched.areBothMatched()) {
                        buffering = true;
                        startedBuffering = true;
                    }
                } else if (event.isCharacters()) {
                    if (buffering && !matched.areBothMatched()) {
                        events.add(event);
                        handled = true;
                        if (context.equalsAny(sel.getStartId())) {
                            startStrings.append(event.asCharacters().getData());
                            allStrings.append(event.asCharacters().getData());
                        } else if (context.equalsAny(sel.getEndId())) {
                            endStrings.append(event.asCharacters().getData());
                            allStrings.append(event.asCharacters().getData());
                        }
                    }
                } else if (event.isEndElement()) {
                    if (context.equalsAny(sel.getStartId(), sel.getEndId())) {
                        flush(writer, !matched.isStartMatched() ? allStrings.toString() : endStrings.toString(), sel, events, context, matched, localId, endOffset);
                        buffering = false;
                        events.clear();
                        allStrings = new StringBuilder();
                    }
                    context.pop();
                    if (buffering && !matched.areBothMatched()) {
                        events.add(event);
                        handled = true;
                    }
                    /* The second comparison is to verify that we only start buffering if we are not going to have the chance
                     * to pass the start element of the start/end block and restart the buffering then. */
                    if (startedBuffering && (sel.isStartChildOfEnd() || sel.isEndChildOfStart())) {
                        buffering = !matched.areBothMatched();
                    }
                }
                if (!handled) {
                    writer.add(event);
                }
            }
        } catch (XMLStreamException e) {
            logger.error("", e);
        } catch (CloneNotSupportedException e) {
            logger.error("", e);
        } finally {
            try {
                writer.close();
                reader.close();
            } catch (XMLStreamException e) {
                logger.error("", e);
            }
            if (!matched.areBothMatched()) {
                throw new NoteAdditionFailedException(sel, localId, matched.isStartMatched(), matched.isEndMatched());
            }
        }
    }

    private Document createDocument(String path, String title, String description) {
        Document doc = new Document();
        doc.setSvnPath(path);
        doc.setTitle(title);
        doc.setDescription(description);
        getSession().save(doc);
        return doc;
    }

    private void flush(XMLEventWriter writer, String string, SelectedText sel, List<XMLEvent> events, ElementContext context, Matched matched, String localId, MutableInt endOffset) throws XMLStreamException {
        String startAnchor = "start"+localId;
        String endAnchor = "end"+localId;
        boolean startAndEndInSameElement = sel.getStartId().equals(sel.getEndId());
        int offset = 0;
        int startIndex = getIndex(string, sel.getFirstWord(), sel.getStartIndex());
        int endIndex = getIndex(string, sel.getLastWord(), sel.getEndIndex()) + sel.getLastWord().length();
        for (XMLEvent e : events) {
            boolean handled = false;
            if (e.isStartElement()) {
                context.push(extractName(e.asStartElement()));
            } else if (e.isEndElement()) {
                context.pop();
            } else if (e.isCharacters() && context.equalsAny(sel.getStartId(), sel.getEndId())) {
                String eventString = e.asCharacters().getData();
                int relativeStart = startIndex - offset;
                int relativeEnd = endIndex - (context.equalsAny(sel.getEndId()) && sel.isStartChildOfEnd() ? endOffset.intValue() : offset);
                int index = -1;
                offset += eventString.length();
                if (context.equalsAny(sel.getEndId()) && sel.isStartChildOfEnd()) {
                    endOffset.add(eventString.length());
                }
                if (context.equalsAny(sel.getStartId()) && !matched.isStartMatched() && startIndex <= offset) {
                    writer.add(eventFactory.createCharacters(eventString.substring(0, relativeStart)));
                    writeAnchor(writer, startAnchor);
                    matched.matchStart();
                    handled = true;
                    index = relativeStart;
                }
                if (context.equalsAny(sel.getEndId()) && matched.isStartMatched()
                        && !matched.isEndMatched() && endIndex <= (context.equalsAny(sel.getEndId()) && sel.isStartChildOfEnd() ? endOffset.intValue() : offset)) {
                    if (!startAndEndInSameElement) {
                        writer.add(eventFactory.createCharacters(eventString.substring(0,relativeEnd)));
                    } else {
                        /* relativeStart might be negative which means that it is not in the current
                         * eventString, in this case we start the character writing from the
                         * beginning of the eventString.
                         */
                        writer.add(eventFactory.createCharacters(eventString.substring(
                                relativeStart > -1 ? relativeStart : 0, relativeEnd)));
                    }
                    writeAnchor(writer, endAnchor);
                    matched.matchEnd();
                    handled = true;
                    index = relativeEnd;
                }
                if (handled) {
               	    // TODO : skip this, if index == eventString.length() -1
                    writer.add(eventFactory.createCharacters(eventString.substring(index)));
                }
            }
            if (!handled) {
                writer.add(e);
            }
        }
    }

    @Override
    public Collection<Document> getAll() {
        return getDocumentsOfFolder(documentRoot);
    }

    @Override
    public Document getDocumentForPath(String svnPath) {
        Assert.notNull(svnPath, "svnPath was null");
        Document doc = getDocumentMetadata(svnPath);
        if (doc == null) {
            doc = createDocument(svnPath, svnPath.substring(svnPath.lastIndexOf('/') + 1), null);
        }
        return doc;
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
            Document doc = getDocumentMetadata(path);
            if (doc == null) {
                doc = createDocument(path, entry, null);
            }
            documents.add(doc);
        }
        return documents;
    }

    @Override
    public InputStream getDocumentStream(DocumentRevision doc) throws IOException {
        Assert.notNull(doc, "document was null");
        return svnService.getStream(doc.getSvnPath(), doc.getRevision());
    }

    @Override
    public List<RevisionInfo> getRevisions(Document doc) {
        Assert.notNull(doc, "document was null");
        return svnService.getRevisions(doc.getSvnPath());
    }


    @Override
    public void removeAll(Collection<Document> documents) {
        for (Document document : documents){
            remove(document);
        }
    }

    @Override
    public void remove(Document doc) {
        Assert.notNull(doc, "document was null");

        // delete from svn
        svnService.delete(doc.getSvnPath());

        // delete related document notes
        QDocumentNote documentNote = QDocumentNote.documentNote;
        Session session = getSession();
        List<DocumentNote> documentNotes = session.from(documentNote)
            .where(documentNote.document().eq(doc)).list(documentNote);

        if (!documentNotes.isEmpty()){
            session.deleteAll(documentNotes.toArray());
        }
    }

    @Override
    public DocumentRevision removeAllNotes(Document doc) {
        long revision = svnService.getLatestRevision(doc.getSvnPath());
        DocumentRevision docRevision = doc.getRevision(revision);
        List<DocumentNote> noteRevisions = documentNoteRepository.getOfDocument(docRevision);
        DocumentNote[] notes = new DocumentNote[noteRevisions.size()];
        for (int i = 0; i < notes.length; i++){
            notes[i] = noteRevisions.get(i);
        }
        return removeNotes(docRevision, notes);
    }

    @Override
    public DocumentRevision removeNotes(DocumentRevision docRevision, final DocumentNote... notes){
        long newRevision;
        newRevision = svnService.commit(docRevision.getSvnPath(), docRevision.getRevision(),
                authService.getUsername(), new UpdateCallback() {
                    @Override
                    public void update(InputStream source, OutputStream target) {
                        try {
                            streamEvents(inFactory.createFilteredReader(inFactory
                                    .createXMLEventReader(source), createRemoveFilter(notes)),
                                    outFactory.createXMLEventWriter(target));
                        } catch (XMLStreamException e) {
                            throw new ServiceException(e);
                        }
                    }
                });

        // persisted noteRevision has svnRevision of newly created commit
        for (DocumentNote note : notes) {
            noteRepository.remove(note, newRevision);
        }

        return new DocumentRevision(docRevision, newRevision);
    }

    @Override
    public DocumentRevision removeNotesPermanently(DocumentRevision docRevision, final DocumentNote... notes){
        long newRevision;
        newRevision = svnService.commit(docRevision.getSvnPath(), docRevision.getRevision(),
                authService.getUsername(), new UpdateCallback() {
                    @Override
                    public void update(InputStream source, OutputStream target) {
                        try {
                            streamEvents(inFactory.createFilteredReader(inFactory
                                    .createXMLEventReader(source), createRemoveFilter(notes)),
                                    outFactory.createXMLEventWriter(target));
                        } catch (XMLStreamException e) {
                            throw new ServiceException(e);
                        }
                    }
                });

        // persisted noteRevision has svnRevision of newly created commit
        for (DocumentNote note : notes) {
            noteRepository.removePermanently(note);
        }

        return new DocumentRevision(docRevision, newRevision);
    }


    public void streamEvents(XMLEventReader reader, XMLEventWriter writer) throws XMLStreamException {
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
    public DocumentNote updateNote(final DocumentNote note, final SelectedText selection) throws IOException {
        Document doc = note.getDocument();
        if (note.getLocalId() == null) {
            note.setLocalId(String.valueOf(timeService.currentTimeMillis()));
        }
        long newRevision;
        newRevision = svnService.commit(doc.getSvnPath(), note.getSVNRevision(), authService
                .getUsername(), new UpdateCallback() {
            @Override
            public void update(InputStream source, OutputStream target) {
                try {
                    XMLEventReader eventReader = inFactory.createFilteredReader(inFactory
                            .createXMLEventReader(source), createRemoveFilter(new DocumentNote [] { note }));
                    addNote(eventReader, outFactory.createXMLEventWriter(target), selection, note.getLocalId());
                } catch (XMLStreamException e) {
                    throw new ServiceException(e);
                } catch (NoteAdditionFailedException e) {
                    throw new ServiceException(e);
                }
            }
        });

        // FIXME This logic might be broken.
        DocumentNote copy = note.createCopy();
        copy.setLongText(selection.getSelection());
        copy.setSVNRevision(newRevision);
//        documentNoteRepository.save(copy);
        note.setReplacedBy(copy);
        documentNoteRepository.save(note);

        return copy;
    }

    private void writeAnchor(XMLEventWriter writer, String anchorId) throws XMLStreamException{
        writer.add(eventFactory.createStartElement("", TEI_NS, "anchor"));
        writer.add(eventFactory.createAttribute("xml", XML_NS, "id", anchorId));
        writer.add(eventFactory.createEndElement("", TEI_NS, "anchor"));
    }


}
