/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QDocument.document;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCopySource;

import fi.finlit.edith.EDITH;
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
    
    @Inject
    private SVNClientManager clientManager;
    
//    private File repoFile;
    
    private SVNURL repoURL;
    
    private SVNURL documentsURL;
    
    public DocumentRepositoryImpl() throws SVNException {
        super(document);
        SVNRepositoryFactoryImpl.setup();
//        repoFile = new File(System.getProperty(EDITH.REPO_FILE_PROPERTY));
        repoURL = SVNURL.parseURIEncoded(System.getProperty(EDITH.REPO_URL_PROPERTY));
        documentsURL = repoURL.appendPath("documents/trunk", false);
    }

    @Override
    public Collection<Document> getAll() {
        // TODO : finish this
        try {            
            File destFolder = new File("target");
//            SVNCopySource(SVNRevision pegRevision, SVNRevision revision, SVNURL url)
            SVNCopySource[] sources = null;
            clientManager.getCopyClient().doCopy(sources, destFolder, false, false, false);
            return null;
        } catch (SVNException e) {
            String error = "Caught " + e.getClass().getName();
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }
    
    @Override
    public Document getDocumentForPath(String svnPath) {
        return getSession().from(document)
            .where(document.svnPath.eq(svnPath))
            .uniqueResult(document);
    }

    @Override
    public List<Document> getDocumentsOfFolder(String svnFolder) {
        return getSession().from(document)
            .where(document.svnPath.startsWith(svnFolder))
            .list(document);
    }

}
