/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import net.jcip.annotations.Immutable;

/**
 * DocumentRevision provides
 *
 * @author tiwe
 * @version $Id$
 */
@Immutable
public class DocumentRevision {
    
    private final long revision;
    
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

}
