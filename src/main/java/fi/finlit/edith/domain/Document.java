/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

/**
 * Document provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITH.NS)
public class Document extends Identifiable{
    
    @Predicate
    private String description;

    @Predicate
    private String svnPath;

    @Predicate
    private String title;

    public String getDescription() {
        return description;
    }

    public String getSvnPath() {
        return svnPath;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setSvnPath(String svnPath) {
        this.svnPath = svnPath;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

}
