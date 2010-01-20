/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QDocument.document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import com.mysema.rdfbean.dao.AbstractRepository;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;

/**
 * DocumentRepositoryImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRepositoryImpl extends AbstractRepository<Document> implements DocumentRepository{

    private static final Logger logger = LoggerFactory.getLogger(DocumentRepositoryImpl.class);
    
    @Inject
    private SubversionService svnService;

    @Inject 
    @Symbol(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;
    
    public DocumentRepositoryImpl() throws SVNException {
        super(document);
    }

    @Override
    public void addDocument(String svnPath, File file) throws SVNException {
        svnService.commit(svnPath, file);        
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
    
    @Override
    public File getDocumentFile(DocumentRevision document) throws IOException {
        try {
            return svnService.getFile(document.getSvnPath(), document.getRevision());            
        } catch (SVNException e) {
            String error = "Caught " + e.getClass().getName();
            logger.error(error, e);
            throw new IOException(error, e);
        }
    }
    
    @Override
    public Document getDocumentForPath(String svnPath) {
        Document document = getDocumentMetadata(svnPath);
        if (document == null){
            document = createDocument(svnPath, svnPath.substring(svnPath.lastIndexOf('/')+1), null);
        }
        return document;
    }
    
    private Document getDocumentMetadata(String svnPath){
        return getSession().from(document)
            .where(document.svnPath.eq(svnPath))
            .uniqueResult(document);
    }
    
    @Override
    public List<Document> getDocumentsOfFolder(String svnFolder) {
        try {
            Collection<String> entries = svnService.getEntries(svnFolder, /* HEAD */ -1);
            List<Document> documents = new ArrayList<Document>(entries.size());
            for (String entry : entries){
                String path = svnFolder + "/" + entry;
                Document document = getDocumentMetadata(path);
                if (document == null){
                    document = createDocument(path, entry, null);
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

    @Override
    public List<Long> getRevisions(Document document) throws SVNException {
        return svnService.getRevisions(document.getSvnPath());
    }
    
    @Override
    public void remove(Document document){
        svnService.remove(document.getSvnPath());  
    }

}
