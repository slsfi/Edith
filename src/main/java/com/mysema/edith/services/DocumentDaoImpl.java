/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.Transactional;
import com.mysema.edith.EDITH;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.QDocument;
import com.mysema.edith.domain.QDocumentNote;
import com.mysema.edith.dto.FileItem;
import com.mysema.edith.dto.FileItemWithDocumentId;
import com.mysema.edith.dto.SelectedText;
import com.mysema.edith.util.ElementContext;

// TODO: It would make sense to move XML parsing and all SVN operations
// to other low-level classes and use a service to achieve what this
// class currently does.
@Transactional
public class DocumentDaoImpl extends AbstractDao<Document> implements DocumentDao {

    private static final QDocument document = QDocument.document;

    private static final QDocumentNote documentNote = QDocumentNote.documentNote;

    private static final Logger logger = LoggerFactory.getLogger(DocumentDaoImpl.class);

    private static final String XML_NS = "http://www.w3.org/XML/1998/namespace";

    private static final String TEI_NS = "http://www.tei-c.org/ns/1.0";

    private static final QName XML_ID_QNAME = new QName(XML_NS, "id");

    private static final QName TEI_TYPE_QNAME = new QName(null, "type");

    private final String documentRoot;

    private final SubversionService versioningService;

    private final NoteDao noteDao;

    private final AuthService authService;

    private final DocumentNoteDao documentNoteDao;

    private final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    private final XMLInputFactory inFactory = XMLInputFactory.newInstance();

    private final XMLOutputFactory outFactory = XMLOutputFactory.newInstance();

    @Inject
    public DocumentDaoImpl(SubversionService versioningService, AuthService authService,
            NoteDao noteDao, DocumentNoteDao documentNoteDao,
            @Named(EDITH.SVN_DOCUMENT_ROOT) String documentRoot) {
        this.documentRoot = documentRoot;
        this.versioningService = versioningService;
        this.authService = authService;
        this.noteDao = noteDao;
        this.documentNoteDao = documentNoteDao;
    }

    @Override
    public Document getById(Long id) {
        return query().from(document).where(document.id.eq(id)).singleResult(document);
    }

    @Override
    public void addDocument(String path, File file) {
        versioningService.importFile(path, file);
    }

    @Override
    public int addDocumentsFromZip(String parentPath, File file) {
        try {
            String parent = parentPath.endsWith("/") ? parentPath : parentPath + "/";
            ZipFile zipFile = new ZipFile(file);
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            int rv = 0;
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                if (!entry.getName().endsWith(".xml")) {
                    continue;
                }
                InputStream in = zipFile.getInputStream(entry);
                File outFile = File.createTempFile("tei", ".xml");
                OutputStream out = new FileOutputStream(outFile);
                try {
//                    IOUtils.copy(in, out);
                    ByteStreams.copy(in, out);
                } finally {
                    in.close();
                    out.close();
                }
                addDocument(parent + entry.getName(), outFile);
                outFile.delete();
                rv++;
            }
            return rv;
        } catch (IOException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public DocumentNote addNote(Note note, Document document, final SelectedText selection) {
        final DocumentNote documentNote = new DocumentNote();
        persist(documentNote);
        final AtomicInteger position = new AtomicInteger(0);
        versioningService.commit(document.getPath(), -1, authService.getUsername(),
                new UpdateCallback() {
                    @Override
                    public void update(InputStream source, OutputStream target) throws IOException {
                        try {
                            position.set(addNote(inFactory.createXMLEventReader(source),
                                    outFactory.createXMLEventWriter(target), selection,
                                    documentNote.getId()));
                        } catch (XMLStreamException e) {
                            throw new ServiceException(e);
                        } catch (NoteAdditionFailedException e) {
                            throw new ServiceException(e);
                        }
                    }
                });

        DocumentNote updatedDocumentNote = noteDao.createDocumentNote(documentNote, note, document,
                selection.getSelection(), position.intValue());
        return updatedDocumentNote;
    }

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

    public static String extractName(StartElement element) {
        String localName = element.getName().getLocalPart();
        String name = localName;
        if (localName.equals("div")) {
            Attribute attribute = element.getAttributeByName(TEI_TYPE_QNAME);
            if (attribute != null) {
                name = attribute.getValue();
            }
        }
        return name;
    }

    public int addNote(XMLEventReader reader, XMLEventWriter writer, SelectedText sel, Long localId)
            throws NoteAdditionFailedException {
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
        AtomicInteger endOffset = new AtomicInteger(0);

        // Some sort of absolute position to have elements in order
        AtomicInteger position = new AtomicInteger(0);

        Matched matched = new Matched();
        try {
            boolean buffering = false;
            /*
             * This boolean is used as a flag to signal if the buffering has
             * been started at some point. This is because we don't want to
             * buffer the whole document which would happen in isEndElement else
             * if. FIXME I find this a bit ugly.
             */
            boolean startedBuffering = false;
            while (reader.hasNext()) {
                boolean handled = false;
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    context.push(extractName(event.asStartElement()));
                    if (buffering && !matched.areBothMatched()) {
                        handled = true;
                        if (context.equalsAny(sel.getStartId())) {
                            /*
                             * If the end element is inside the start element,
                             * we want to flush the end elements that do not
                             * contain the desired end anchor position.
                             */
                            ElementContext tempContext = (ElementContext) context.clone();
                            /*
                             * tempContext is used so that we can send the
                             * actual context in most of these use cases. The
                             * first pop is always mandatory, the following ones
                             * only if we are even deeper in.
                             */
                            tempContext.pop();
                            if (sel.isStartChildOfEnd()) {
                                for (int i = 1; i < sel.howDeepIsStartInEnd(); ++i) {
                                    tempContext.pop();
                                }
                            }
                            flush(writer, position, endStrings.toString(), sel, events,
                                    tempContext, matched, localId, endOffset);
                            allStrings = new StringBuilder();
                            events.clear();
                            handled = false;
                        } else if (context.equalsAny(sel.getEndId())) {
                            /*
                             * If the start element is inside the end element,
                             * we want to flush the start elements once reaching
                             * the element containing the end anchor.
                             */
                            ElementContext tempContext = (ElementContext) context.clone();
                            tempContext.pop();
                            if (sel.isEndChildOfStart()) {
                                for (int i = 1; i < sel.howDeepIsEndInStart(); ++i) {
                                    tempContext.pop();
                                }
                            }
                            flush(writer, position, startStrings.toString(), sel, events,
                                    tempContext, matched, localId, endOffset);
                            allStrings = new StringBuilder();
                            events.clear();
                            handled = false;
                        } else {
                            events.add(event);
                        }
                    }
                    if (context.equalsAny(sel.getStartId(), sel.getEndId())
                            && !matched.areBothMatched()) {
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

                    if (!buffering) {
                        position.addAndGet(event.asCharacters().getData().length());
                    }

                } else if (event.isEndElement()) {
                    if (context.equalsAny(sel.getStartId(), sel.getEndId())) {
                        flush(writer, position, !matched.isStartMatched() ? allStrings.toString()
                                : endStrings.toString(), sel, events, context, matched, localId,
                                endOffset);
                        buffering = false;
                        events.clear();
                        allStrings = new StringBuilder();
                    }
                    context.pop();
                    if (buffering && !matched.areBothMatched()) {
                        events.add(event);
                        handled = true;
                    }
                    /*
                     * The second comparison is to verify that we only start
                     * buffering if we are not going to have the chance to pass
                     * the start element of the start/end block and restart the
                     * buffering then.
                     */
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
                throw new NoteAdditionFailedException(sel, String.valueOf(localId),
                        matched.isStartMatched(), matched.isEndMatched());
            }
        }
        return position.intValue();
    }

    public static int getIndex(String str, String word, int occurrence) {
        int index = -1;
        int n = occurrence;
        while (n > 0) {
            index = str.indexOf(word, index + 1);
            if (index == -1) {
                return -1;
            }
            n--;
        }
        return index;
    }

    private void flush(XMLEventWriter writer, AtomicInteger position, String string, SelectedText sel,
            List<XMLEvent> events, ElementContext context, Matched matched, Long localId,
            AtomicInteger endOffset) throws XMLStreamException {
        String startAnchor = "start" + localId;
        String endAnchor = "end" + localId;
        boolean startAndEndInSameElement = sel.getStartId().equals(sel.getEndId());
        int offset = 0;
        int startIndex = getIndex(string, sel.getFirstWord(), sel.getStartIndex());
        int endIndex = getIndex(string, sel.getLastWord(), sel.getEndIndex())
                + sel.getLastWord().length();
        for (XMLEvent e : events) {
            boolean handled = false;
            if (e.isStartElement()) {
                context.push(extractName(e.asStartElement()));
            } else if (e.isEndElement()) {
                context.pop();
            } else if (e.isCharacters() && context.equalsAny(sel.getStartId(), sel.getEndId())) {
                String eventString = e.asCharacters().getData();
                int relativeStart = startIndex - offset;
                int relativeEnd = endIndex
                        - (context.equalsAny(sel.getEndId()) && sel.isStartChildOfEnd() ? endOffset
                                .intValue() : offset);
                int index = -1;
                offset += eventString.length();
                if (context.equalsAny(sel.getEndId()) && sel.isStartChildOfEnd()) {
                    endOffset.addAndGet(eventString.length());
                }
                if (context.equalsAny(sel.getStartId()) && !matched.isStartMatched()
                        && startIndex <= offset) {
                    position.addAndGet(relativeStart);
                    writer.add(eventFactory.createCharacters(eventString
                            .substring(0, relativeStart)));
                    writeAnchor(writer, startAnchor);
                    matched.matchStart();
                    handled = true;
                    index = relativeStart;
                }
                if (context.equalsAny(sel.getEndId())
                        && matched.isStartMatched()
                        && !matched.isEndMatched()
                        && endIndex <= (context.equalsAny(sel.getEndId())
                                && sel.isStartChildOfEnd() ? endOffset.intValue() : offset)) {
                    if (!startAndEndInSameElement) {
                        writer.add(eventFactory.createCharacters(eventString.substring(0,
                                relativeEnd)));
                    } else {
                        /*
                         * relativeStart might be negative which means that it
                         * is not in the current eventString, in this case we
                         * start the character writing from the beginning of the
                         * eventString.
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
                if (e.isCharacters() && !matched.isStartMatched()) {
                    position.addAndGet(e.asCharacters().getData().length());
                }

                writer.add(e);
            }
        }
    }

    private void writeAnchor(XMLEventWriter writer, String anchorId) throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", TEI_NS, "anchor"));
        writer.add(eventFactory.createAttribute("xml", XML_NS, "id", anchorId));
        writer.add(eventFactory.createEndElement("", TEI_NS, "anchor"));
    }

    @Override
    public Document getDocumentForPath(String svnPath) {
        return getDocumentMetadata(svnPath);
    }

    @Override
    public List<Document> getDocumentsOfFolder(String svnFolder) {
        Map<String, String> entries = versioningService.getEntries(svnFolder, /* HEAD */-1);
        List<Document> documents = new ArrayList<Document>(entries.size());
        for (String path : entries.keySet()) {
            documents.add(getDocumentMetadata(path));
        }
        return documents;
    }

    private Document getDocumentMetadata(String path) {
        Document doc = query().from(document).where(document.path.eq(path)).uniqueResult(document);
        if (doc != null) {
            return doc;
        } else {
            return createDocument(path, path.substring(path.lastIndexOf('/') + 1));
        }
    }

    private Document createDocument(String path, String title) {
        Document doc = new Document();
        doc.setPath(path);
        doc.setTitle(title);
        persist(doc);
        return doc;
    }

    @Override
    public InputStream getDocumentStream(Document document) throws IOException {
        return versioningService.getStream(document.getPath(), -1);
    }

    @Override
    public void removeDocumentNotes(Document document, final DocumentNote... documentNotes) {
        long revision;
        revision = versioningService.commit(document.getPath(), -1, authService.getUsername(),
                new UpdateCallback() {
                    @Override
                    public void update(InputStream source, OutputStream target) {
                        try {
                            streamEvents(inFactory.createFilteredReader(
                                    inFactory.createXMLEventReader(source),
                                    createRemoveFilter(documentNotes)), outFactory
                                    .createXMLEventWriter(target));
                        } catch (XMLStreamException e) {
                            throw new ServiceException(e);
                        }
                    }
                });

        for (DocumentNote dn : documentNotes) {
            dn.setRevision(revision);
            documentNoteDao.remove(dn);
        }
    }

    private void streamEvents(XMLEventReader reader, XMLEventWriter writer)
            throws XMLStreamException {
        try {
            while (reader.hasNext()) {
                writer.add(reader.nextEvent());
            }
        } finally {
            writer.close();
            reader.close();
        }
    }

    private static EventFilter createRemoveFilter(DocumentNote... documentNotes) {
        final Set<String> anchors = new HashSet<String>(documentNotes.length * 2);

        for (DocumentNote note : documentNotes) {
            anchors.add("start" + note.getId());
            anchors.add("end" + note.getId());
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
    public DocumentNote updateNote(final DocumentNote documentNote, final SelectedText selection)
            throws IOException {
        Document doc = documentNote.getDocument();
        long newRevision;
        final AtomicInteger position = new AtomicInteger(0);
        newRevision = versioningService.commit(doc.getPath(), -1, authService.getUsername(),
                new UpdateCallback() {
                    @Override
                    public void update(InputStream source, OutputStream target) {
                        try {
                            XMLEventReader eventReader = inFactory.createFilteredReader(
                                    inFactory.createXMLEventReader(source),
                                    createRemoveFilter(new DocumentNote[] { documentNote }));
                            position.set(addNote(eventReader,
                                    outFactory.createXMLEventWriter(target), selection,
                                    documentNote.getId()));
                        } catch (XMLStreamException e) {
                            throw new ServiceException(e);
                        } catch (NoteAdditionFailedException e) {
                            throw new ServiceException(e);
                        }
                    }
                });
        DocumentNote fetchedDocumentNote = find(DocumentNote.class, documentNote.getId());
        fetchedDocumentNote.setFullSelection(selection.getSelection());
        fetchedDocumentNote.setRevision(newRevision);
        fetchedDocumentNote.setPosition(position.intValue());
        return fetchedDocumentNote;
    }

    @Override
    public void remove(Document doc) {
        versioningService.delete(doc.getPath());
        delete(documentNote).where(documentNote.document.eq(doc)).execute();
        super.remove(doc);
    }

    @Override
    public void remove(Long id) {
        Document document = find(Document.class, id);
        if (document != null) {
            remove(document);
        }
    }

    @Override
    public void removeAll(Collection<Document> documents) {
        for (Document document : documents) {
            remove(document);
        }
    }

    @Override
    public void rename(Long id, String newPath) {
        Document doc = getById(id);
        String fullPath = doc.getPath();
        String directoryPath = fullPath.substring(0, fullPath.lastIndexOf('/') + 1);
        List<Document> documents = query().from(document)
                .where(document.path.contains(doc.getPath())).list(document);
        for (Document d : documents) {
            if (!d.getId().equals(id)) {
                d.setPath(d.getPath().replace(doc.getPath(), directoryPath + newPath));
            }
        }
        versioningService.move(fullPath, directoryPath + newPath);
        doc.setPath(directoryPath + newPath);
        doc.setTitle(newPath.substring(newPath.lastIndexOf('/') + 1));
    }

    @Override
    public void save(Document doc) {
        persist(doc);
    }

    @Override
    public List<FileItemWithDocumentId> fromPath(String path, Long id) {
        List<FileItem> files = Strings.isNullOrEmpty(path) ? versioningService.getFileItems(
                documentRoot, -1) : versioningService.getFileItems(path, -1);
        List<FileItemWithDocumentId> rv = new ArrayList<FileItemWithDocumentId>();
        for (FileItem file : files) {
            Document doc = getDocumentForPath(file.getPath());
            rv.add(new FileItemWithDocumentId(file.getTitle(), file.getPath(), file.getIsFolder(),
                    file.getChildren(), file.getHasChildren(), doc.getId(), doc.getId().equals(id),
                    documentNoteDao.getNoteCountForDocument(doc.getId())));
        }
        Collections.sort(rv, new Comparator<FileItemWithDocumentId>() {
            @Override
            public int compare(FileItemWithDocumentId o1, FileItemWithDocumentId o2) {
                if (o1.getIsFolder() && !o2.getIsFolder()) {
                    return -1;
                } else if (!o1.getIsFolder() && o2.getIsFolder()) {
                    return 1;
                }
                return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
            }
        });
        return rv;
    }

}