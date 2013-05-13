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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.Transactional;
import com.mysema.edith.EDITH;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.NoteComment;
import com.mysema.edith.domain.QDocument;
import com.mysema.edith.domain.QDocumentNote;
import com.mysema.edith.domain.QNote;
import com.mysema.edith.domain.QNoteComment;
import com.mysema.edith.dto.FileItem;
import com.mysema.edith.dto.FileItemWithDocumentId;
import com.mysema.edith.dto.SelectedText;

@Transactional
public class DocumentDaoImpl extends AbstractDao<Document> implements DocumentDao {

    private static final QDocument document = QDocument.document;

    private static final QDocumentNote documentNote = QDocumentNote.documentNote;

    private static final QNote note = QNote.note;

    private final String documentRoot;

    private final VersioningDao versioningService;

    private final NoteDao noteDao;

    private final AuthService authService;

    private final DocumentNoteDao documentNoteDao;
    
    private final DocumentXMLDao xmlDao;

    @Inject
    public DocumentDaoImpl(VersioningDao versioningService, AuthService authService,
            NoteDao noteDao, DocumentNoteDao documentNoteDao,
            DocumentXMLDao xmlDao,
            @Named(EDITH.SVN_DOCUMENT_ROOT) String documentRoot) {
        this.documentRoot = documentRoot;
        this.versioningService = versioningService;
        this.authService = authService;
        this.noteDao = noteDao;
        this.xmlDao = xmlDao;  
        this.documentNoteDao = documentNoteDao;
    }

    @Override
    public Document getById(Long id) {        
        return find(Document.class, id);
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
                        position.set(xmlDao.addNote(source, target, selection, documentNote));
                    }
                });

        DocumentNote updatedDocumentNote = noteDao.createDocumentNote(documentNote, note, document,
                selection.getSelection(), position.intValue());
        return updatedDocumentNote;
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
        Document doc = from(document).where(document.path.eq(path)).uniqueResult(document);
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
                        xmlDao.removeNotes(source, target, documentNotes);
                    }
                });

        for (DocumentNote dn : documentNotes) {
            dn.setRevision(revision);
            documentNoteDao.remove(dn);
        }
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
                        position.set(xmlDao.updateNote(source, target, selection, documentNote));
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
        List<Document> documents = from(document)
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
    public Document save(Document document) {
        return persistOrMerge(document);
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

    @Override
    public List<NoteComment> getNoteComments(long id, long limit) {
        final QNoteComment noteComment = QNoteComment.noteComment;
        List<NoteComment> noteComments = query()
                .from(note, documentNote, noteComment)
                .where(documentNote.document.id.eq(id),
                       documentNote.deleted.isFalse(),
                       documentNote.note.eq(note),
                       noteComment.in(note.comments))
                .orderBy(noteComment.createdAt.desc())
                .limit(limit)
                .list(noteComment);
        return noteComments;
    }
}