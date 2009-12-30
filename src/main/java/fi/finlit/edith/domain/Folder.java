/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.InverseFunctionalProperty;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

/**
 * Folder provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITH.NS)
public class Folder extends Identifiable implements Comparable<Folder>{

    @Predicate
    @InverseFunctionalProperty
    private String svnPath;
    
    @Override
    public int compareTo(Folder folder) {
        return svnPath.compareTo(folder.svnPath);
    }

    public String getSvnPath() {
        return svnPath;
    }

    public void setSvnPath(String svnPath) {
        this.svnPath = svnPath;
    }
    
    

}
