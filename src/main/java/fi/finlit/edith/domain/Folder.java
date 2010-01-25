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
 * Folder provides
 *
 * @author tiwe
 * @version $Id$
 */
// NOTE : do we need a Folder type at all ?!?
@ClassMapping(ns=EDITH.NS)
public class Folder extends Identifiable implements Comparable<Folder>{

    @Predicate
    @Unique
    private String svnPath;
    
    @Override
    public int compareTo(Folder folder) {
        return svnPath.compareTo(folder.svnPath);
    }
    
    @Override
    public int hashCode(){
        return svnPath.hashCode();
    }
    
    @Override
    public boolean equals(Object o){
        return o instanceof Folder && ((Folder)o).svnPath.equals(svnPath);
    }

    public String getSvnPath() {
        return svnPath;
    }

    public void setSvnPath(String svnPath) {
        this.svnPath = svnPath;
    }
    
    

}
