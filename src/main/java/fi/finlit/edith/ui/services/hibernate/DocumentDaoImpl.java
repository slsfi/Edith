/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.hibernate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.dto.SelectedText;
import fi.finlit.edith.sql.domain.Document;
import fi.finlit.edith.sql.domain.QDocument;
import fi.finlit.edith.ui.services.DocumentDao;
import fi.finlit.edith.ui.services.NoteAdditionFailedException;
import fi.finlit.edith.ui.services.svn.FileItemWithDocumentId;
import fi.finlit.edith.ui.services.svn.RevisionInfo;
import static fi.finlit.edith.sql.domain.QDocument.document;

// TODO: It would make sense to move XML parsing and all SVN operations
// to other low-level classes and use a service to achieve what this
// class currently does.
public class DocumentDaoImpl implements DocumentDao {
    private JPQLQuery query() {
        return new HibernateQuery(session);
    }

    @Inject
    private Session session;



    @Override
    public Collection<Document> getAll() {
        return query().from(document).list(document);
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
    public Document getOrCreateDocumentForPath(String svnPath) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Document getDocumentForPath(String svnPath) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Document> getDocumentsOfFolder(String svnFolder) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getDocumentStream(Document document) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RevisionInfo> getRevisions(Document document) {
        // TODO Auto-generated method stub
        return null;
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