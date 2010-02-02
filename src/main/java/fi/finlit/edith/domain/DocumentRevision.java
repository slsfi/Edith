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
    
    private final Document document;
    
    public DocumentRevision(Document document, long revision){
        this.document = document;
        this.revision = revision;
    }
    
    public DocumentRevision(DocumentRevision docRevision, long revision) {
        this(docRevision.getDocument(), revision);
    }

    public Document getDocument() {
        return document;
    }

    public long getRevision() {
        return revision;
    }

    public String getSvnPath() {
        return document.getSvnPath();
    }

    public void setRevision(long newRevision) {
        this.revision = newRevision;
        
    }

}
