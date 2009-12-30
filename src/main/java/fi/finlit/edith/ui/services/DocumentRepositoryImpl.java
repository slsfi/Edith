/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QDocument.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;

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
    @Symbol(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;
    
    @Inject 
    @Symbol(EDITH.SVN_CACHE_DIR)
    private File svnCache;
    
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

    @Override
    public File getDocumentFile(DocumentRevision document) throws IOException {
        try {
            long revision = document.getRevision();
            if (revision == -1){
                revision = getLatestRevision(document.getSvnPath());
            }
            return getDocumentFile(document.getSvnPath(), revision);            
        } catch (SVNException e) {
            String error = "Caught " + e.getClass().getName();
            logger.error(error, e);
            throw new IOException(error, e);
        }
    }
    
    private File getDocumentFile(String svnPath, long revision) throws IOException, SVNException{
        File documentFolder = new File(svnCache, URLEncoder.encode(svnPath,"UTF-8")); 
        File documentFile = new File(documentFolder, String.valueOf(revision));
        if (!documentFile.exists()){
            documentFolder.mkdirs();
            svnRepository.getFile(svnPath, revision, null, new FileOutputStream(documentFile));
        }                
        return documentFile;
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
    
    private long getLatestRevision(String svnPath) throws SVNException{
        long revision = 0;
        long latest = svnRepository.getLatestRevision();
        Collection<SVNFileRevision> revisions = new ArrayList<SVNFileRevision>(); 
        svnRepository.getFileRevisions(svnPath, revisions, 0, latest);
        for (SVNFileRevision rev : revisions){
            if (revision < rev.getRevision()){
                revision = rev.getRevision();
            }
        }
        return revision;
    }

}
