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
     * @param document
     * @param svnRevision
     * @param startId
     * @param endId
     * @param text
     * @return
     * @throws IOException 
     */
    Note addNote(Document document, long svnRevision, String startId, String endId, String text) throws IOException;
    
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
     */
    List<Long> getRevisions(Document document);

    /**
     * @param string
     * @param file
     */
    void addDocument(String svnPath, File file);

}
