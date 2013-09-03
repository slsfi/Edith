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

import javax.annotation.PostConstruct;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.Transactional;
import com.mysema.edith.EDITH;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.NoteComment;
import com.mysema.edith.domain.QDocument;
import com.mysema.edith.domain.QDocumentNote;
import com.mysema.edith.domain.QNote;
import com.mysema.edith.domain.QNoteComment;
import com.mysema.edith.dto.FileItem;
import com.mysema.edith.dto.FileItemWithDocumentId;

@Transactional
public class DocumentDaoImpl extends AbstractDao<Document> implements DocumentDao {

    private static final QDocument document = QDocument.document;

    private static final QDocumentNote documentNote = QDocumentNote.documentNote;

    private static final QNote note = QNote.note;

    private final String documentRoot;

    private final VersioningDao versioningDao;

    private final DocumentNoteDao documentNoteDao;

    @Inject
    public DocumentDaoImpl(VersioningDao versioningDao,
            DocumentNoteDao documentNoteDao,
            @Named(EDITH.SVN_DOCUMENT_ROOT) String documentRoot) {
        this.documentRoot = documentRoot;
        this.versioningDao = versioningDao;
        this.documentNoteDao = documentNoteDao;
    }
    
    @PostConstruct
    public void init() {
        fromPath(null, null);
    }

    @Override
    public Document getById(Long id) {
        return find(Document.class, id);
    }

    @Override
    public Document addDocument(String path, File file) {
        // TODO: Remove try-catch once we have been able to import SLS data.
        try {
            versioningDao.importFile(path, file);
        } catch (VersioningException e) {
            System.err.println("already imported? " + e.getMessage());
        }
        return createDocument(path);
    }

    @Override
    public List<Document> addDocumentsFromZip(String parentPath, File file) {
        try {
            String parent = parentPath.endsWith("/") ? parentPath : parentPath + "/";
            ZipFile zipFile = new ZipFile(file);
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            List<Document> rv = Lists.newArrayList();
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
                Document document = addDocument(parent + entry.getName(), outFile);
                outFile.delete();
                rv.add(document);
            }
            return rv;
        } catch (IOException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Document getDocumentForPath(String path) {
        return getDocumentMetadata(path);
    }

    @Override
    public List<Document> getDocumentsOfFolder(String svnFolder) {
        Map<String, String> entries = versioningDao.getEntries(svnFolder, /* HEAD */-1);
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
        }
        return createDocument(path);
    }

    private Document createDocument(String path) {
        Document doc = from(document).where(document.path.eq(path)).uniqueResult(document);
        if (doc == null) {
            doc = new Document();
            doc.setPath(path);
            doc.setTitle(path.contains("/") ? path.substring(1 + path.lastIndexOf('/')) : path);
            persist(doc);
        }
        return doc;
    }

    @Override
    public InputStream getDocumentStream(Document document) throws IOException {
        return versioningDao.getStream(document.getPath(), -1);
    }

    @Override
    public void remove(Document doc) {
        versioningDao.delete(doc.getPath());
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
    public void removeByPath(String path) {
        boolean directMatch = false;
        for (Document doc : from(document)
                .where(document.path.startsWith(path)).list(document)) {
            directMatch |= doc.getPath().equals(path);
            remove(doc);
        }
        if (!directMatch) {
            versioningDao.delete(path);
        }
    }

    @Override
    public void removeAll(Collection<Document> documents) {
        for (Document document : documents) {
            remove(document);
        }
    }

    @Override
    public Document rename(Long id, String newPath) {
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
        versioningDao.move(fullPath, directoryPath + newPath);
        doc.setPath(directoryPath + newPath);
        doc.setTitle(newPath.substring(newPath.lastIndexOf('/') + 1));
        return doc;
    }

    @Override
    public Document save(Document document) {
        return persistOrMerge(document);
    }

    @Override
    public List<FileItemWithDocumentId> fromPath(String path, Long id) {
        List<FileItem> files = Strings.isNullOrEmpty(path) ? versioningDao.getFileItems(
                documentRoot, -1) : versioningDao.getFileItems(path, -1);
        List<FileItemWithDocumentId> rv = new ArrayList<FileItemWithDocumentId>();
        for (FileItem file : files) {
            Document doc = getDocumentForPath(file.getPath());
            rv.add(new FileItemWithDocumentId(file.getTitle(), file.getPath(), file.getIsFolder(),
                    file.getChildren(), file.getHasChildren(), doc.getId(), doc.getId().equals(id),
                    documentNoteDao.getNoteCountForDocument(doc.getId()),
                    documentNoteDao.getLastNoteTimestampForDocument(doc.getId())));
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
        QNoteComment noteComment = QNoteComment.noteComment;
        List<NoteComment> noteComments = query()
                .from(documentNote)
                .innerJoin(documentNote.note, note)
                .innerJoin(note.comments, noteComment)
                .where(documentNote.document.id.eq(id),
                       documentNote.deleted.isFalse())
                .orderBy(noteComment.createdAt.desc())
                .limit(limit)
                .list(noteComment);
        return noteComments;
    }

}