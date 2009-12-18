/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Id;
import com.mysema.rdfbean.model.IDType;
import com.mysema.rdfbean.model.UID;

import fi.finlit.edith.EDITH;

/**
 * Tag provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITH.NS)
public class Tag {
    
    @Id(IDType.URI)
    UID id;
    
    protected Tag(){}
    
    public Tag(String name){
        this.id = new UID("tag:", name);
    }
    
    public String getName(){
        return id.getLocalName();
    }

}
