/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import java.util.List;

/**
 * DocumentRepository provides
 *
 * @author tiwe
 * @version $Id$
 */
public interface DocumentRepository extends Repository<Document,String>{
    
    /**
     * @param svnPath
     * @return
     */
    Document getDocumentForPath(String svnPath);
    
    /**
     * @param svnFolder
     * @return
     */
    List<Document> getDocumentsOfFolder(String svnFolder);

}
