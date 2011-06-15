/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.hibernate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.hibernate.Session;

import com.mysema.commons.lang.Assert;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.dto.SelectedText;
import fi.finlit.edith.sql.domain.Document;
import fi.finlit.edith.sql.domain.QDocument;
import fi.finlit.edith.ui.services.DocumentDao;
import fi.finlit.edith.ui.services.NoteAdditionFailedException;
import fi.finlit.edith.ui.services.svn.FileItemWithDocumentId;
import fi.finlit.edith.ui.services.svn.RevisionInfo;
import fi.finlit.edith.ui.services.svn.SubversionService;
import static fi.finlit.edith.sql.domain.QDocument.document;

// TODO: It would make sense to move XML parsing and all SVN operations
// to other low-level classes and use a service to achieve what this
// class currently does.
public class DocumentDaoImpl implements DocumentDao {
    private JPQLQuery query() {
        return new HibernateQuery(getSession());
    }

    private Session getSession() {
        return sessionManager.getSession();
    }

    private final HibernateSessionManager sessionManager;

    private final String documentRoot;

    private final SubversionService versioningService;

    public DocumentDaoImpl(
            @Inject Session session,
            @Inject SubversionService versioningService,
            @Inject HibernateSessionManager sessionManager,
            @Inject @Symbol(EDITH.SVN_DOCUMENT_ROOT) String documentRoot) {
        this.sessionManager = sessionManager;
        this.documentRoot = documentRoot;
        this.versioningService = versioningService;
    }

    @Override
    public Collection<Document> getAll() {
        return getDocumentsOfFolder(documentRoot);
    }

    @Override
    public Document getById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addDocument(String svnPath, File file) {
        // TODO Auto-generated method stub

    }

    @Override
    public int addDocumentsFromZip(String parentSvnPath, File file) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public DocumentNote addNote(Note note, Document document, SelectedText selection)
            throws IOException, NoteAdditionFailedException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Document getOrCreateDocumentForPath(String path) {
        Assert.notNull(path, "path was null");
        Document doc = getDocumentMetadata(path);
        if (doc == null) {
            doc = createDocument(path, path.substring(path.lastIndexOf('/') + 1));
        }
        return doc;
    }

    @Override
    public Document getDocumentForPath(String svnPath) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Document> getDocumentsOfFolder(String svnFolder) {
        Assert.notNull(svnFolder, "svnFolder was null");
        Map<String, String> entries = versioningService.getEntries(svnFolder, /* HEAD */-1);
        List<Document> documents = new ArrayList<Document>(entries.size());
        for (Entry<String, String> entry : entries.entrySet()) {
            String path = entry.getKey();
            String title = entry.getValue();
            Document doc = getDocumentMetadata(path);
            if (doc == null) {
                doc = createDocument(path, title);
            }
            documents.add(doc);
        }
        return documents;
    }

    private Document getDocumentMetadata(String path) {
        return query().from(document)
            .where(document.path.eq(path))
            .uniqueResult(document);
    }

    private Document createDocument(String path, String title) {
        Document doc = new Document();
        doc.setPath(path);
        doc.setTitle(title);
        getSession().save(doc);
        return doc;
    }

    @Override
    public InputStream getDocumentStream(Document document) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RevisionInfo> getRevisions(Document document) {
        Assert.notNull(document, "document was null");
        return versioningService.getRevisions(document.getPath());
    }

    @Override
    public void removeAllNotes(Document document) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeNotes(Document document, DocumentNote... notes) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeNotesPermanently(Document document, DocumentNote... notes) {
        // TODO Auto-generated method stub

    }

    @Override
    public DocumentNote updateNote(DocumentNote note, SelectedText selection) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(Document doc) {
        // TODO Auto-generated method stub

    }

    @Override
    public void remove(Long id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAll(Collection<Document> documents) {
        // TODO Auto-generated method stub

    }

    @Override
    public void move(Long id, String newPath) {
        // TODO Auto-generated method stub

    }

    @Override
    public void rename(Long id, String newPath) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<FileItemWithDocumentId> fromPath(String path, Long id) {
        // TODO Auto-generated method stub
        return null;
    }


}