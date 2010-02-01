/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;
import com.mysema.rdfbean.annotations.Unique;

import fi.finlit.edith.EDITH;

/**
 * Document provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITH.NS)
public class Document extends Identifiable implements Comparable<Document>{
    
    @Predicate
    private String description;
    
    @Predicate
    @Unique
    private String svnPath;

    @Predicate
    private String title;

    @Override
    public int compareTo(Document doc) {
        return svnPath.compareTo(doc.svnPath);
    }

    @Override
    public boolean equals(Object o){
        return o instanceof Document && ((Document)o).svnPath.equals(svnPath);
    }

    public String getDescription() {
        return description;
    }

    public String getSvnPath() {
        return svnPath;
    }
    
    public String getTitle() {
        return title;
    }
    
    @Override
    public int hashCode(){
        return svnPath.hashCode();
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
