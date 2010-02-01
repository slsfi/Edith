/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;


/**
 * DocumentRevision provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRevision {
    
    private long revision;
    
    private final String svnPath;
    
    public DocumentRevision(Document document, long revision){
        this(document.getSvnPath(), revision);
    }

    public DocumentRevision(String svnPath, long revision){
        this.svnPath = svnPath;
        this.revision = revision;
    }
    
    public long getRevision() {
        return revision;
    }

    public String getSvnPath() {
        return svnPath;
    }

    public void setRevision(long newRevision) {
        this.revision = newRevision;
        
    }

}
