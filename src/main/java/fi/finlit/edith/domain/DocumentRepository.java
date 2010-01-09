/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNException;

import com.mysema.rdfbean.dao.Repository;

/**
 * DocumentRepository provides
 *
 * @author tiwe
 * @version $Id$
 */
@Transactional
public interface DocumentRepository extends Repository<Document,String>{
    
    /**
     * Get a Document handle for the given path
     * 
     * @param svnPath
     * @return
     */
    Document getDocumentForPath(String svnPath);
    
    /**
     * Get the Documents of the given directory path and its subpaths
     * 
     * @param svnFolder
     * @return
     */
    List<Document> getDocumentsOfFolder(String svnFolder);

    /**
     * @param svnPath
     * @param revision
     * @return
     * @throws IOException 
     */
    File getDocumentFile(DocumentRevision document) throws IOException;

    /**
     * @param document
     * @return
     * @throws SVNException 
     */
    List<Long> getRevisions(Document document) throws SVNException;

    /**
     * @param string
     * @param file
     * @throws SVNException 
     */
    void addDocument(String svnPath, File file) throws SVNException;

}
