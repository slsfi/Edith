/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QDocument.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;

/**
 * DocumentRepositoryImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRepositoryImpl extends AbstractRepository<Document> implements DocumentRepository{

    private static final Logger logger = LoggerFactory.getLogger(DocumentRepositoryImpl.class);
    
    // TODO : make this configurable
    private String documentRoot = "/documents/trunk";
    
    @Inject
    private SVNRepository svnRepository;
    
    public DocumentRepositoryImpl() throws SVNException {
        super(document);
    }

    private Document createDocument(String path, String title, String description){
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
    
    private Document getDocumentMetadata(String svnPath){
        return getSession().from(document)
            .where(document.svnPath.eq(svnPath))
            .uniqueResult(document);
    }
    
    @Override
    public Document getDocumentForPath(String svnPath) {
        Document document = getDocumentMetadata(svnPath);
        if (document == null){
            document = createDocument(svnPath, svnPath.substring(svnPath.lastIndexOf('/')+1), null);
        }
        return document;
    }

    @Override
    public List<Document> getDocumentsOfFolder(String svnFolder) {
        try {
            Collection<SVNDirEntry> entries = new ArrayList<SVNDirEntry>();
            svnRepository.getDir(svnFolder, /* HEAD */ -1, false, entries);
            List<Document> documents = new ArrayList<Document>(entries.size());
            for (SVNDirEntry entry : entries){
                String path = svnFolder + "/" + entry.getName();
                Document document = getDocumentMetadata(path);
                if (document == null){
                    document = createDocument(path, entry.getName(), null);
                }
                documents.add(document);
            }
            return documents;
            
        } catch (SVNException e) {
            String error = "Caught " + e.getClass().getName();
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

}
