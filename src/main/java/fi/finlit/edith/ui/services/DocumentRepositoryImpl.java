/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QDocument.document;

import java.util.List;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;

/**
 * DocumentRepositoryImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRepositoryImpl extends AbstractRepository<Document> implements DocumentRepository{

    private SVNRepository repository;
    
    private String documentsRoot = "documents/trunk";
    
    public DocumentRepositoryImpl() throws SVNException {
        super(document);
        SVNRepositoryFactoryImpl.setup();
        SVNURL url = SVNURL.parseURIDecoded( System.getProperty("svn.repo") );
        repository = SVNRepositoryFactory.create(url, null );
    }

    @Override
    public Document getDocumentForPath(String svnPath) {
        return getSession().from(document).where(document.svnPath.eq(svnPath))
            .uniqueResult(document);
    }

    @Override
    public List<Document> getDocumentsOfFolder(String svnFolder) {
        return getSession().from(document).where(document.svnPath.startsWith(svnFolder))
            .list(document);
    }

}
