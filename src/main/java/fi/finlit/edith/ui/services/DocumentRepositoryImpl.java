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
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;

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
    
    static{
        FSRepositoryFactory.setup();
    }
    
    @Inject
    private SVNClientManager clientManager;
    
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

    @SuppressWarnings("deprecation")
    @Override
    public void addDocument(String svnPath, File file) throws SVNException {
        SVNURL repoURL = svnRepository.getRepositoryRoot(false);
        SVNCommitClient commitClient = clientManager.getCommitClient();
        commitClient.doImport(file, repoURL.appendPath(svnPath, false), svnPath + " added", false);        
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
        List<SVNFileRevision> revisions = getRevisions(svnPath); 
        long revision = 0;
        for (SVNFileRevision rev : revisions){
            if (revision < rev.getRevision()){
                revision = rev.getRevision();
            }
        }
        return revision;
    }
    
    @Override
    public List<Long> getRevisions(Document document) throws SVNException {
        List<SVNFileRevision> revisions = getRevisions(document.getSvnPath());
        List<Long> revisionIds = new ArrayList<Long>(revisions.size());
        for (SVNFileRevision rev : revisions){
            revisionIds.add(rev.getRevision());
        }
        return revisionIds;
    }

    private List<SVNFileRevision> getRevisions(String svnPath) throws SVNException{
        long latest = svnRepository.getLatestRevision();
        List<SVNFileRevision> revisions = new ArrayList<SVNFileRevision>(); 
        svnRepository.getFileRevisions(svnPath, revisions, 0, latest);
        return revisions;
    }
    
    @Override
    public void remove(Document document){
        // only delete document in SVN, do not remove metadata instance
        try {
            SVNURL repoURL = svnRepository.getRepositoryRoot(false);
            SVNURL targetURL = repoURL.appendPath(document.getSvnPath(), false);
            clientManager.getCommitClient().doDelete(new SVNURL[]{targetURL}, "removed " + document.getSvnPath());
        } catch (SVNException e) {
            throw new RuntimeException(e.getMessage(), e);
        }        
    }

}
