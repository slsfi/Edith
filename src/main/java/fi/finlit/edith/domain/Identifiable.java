/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Id;
import com.mysema.rdfbean.model.IDType;

@ClassMapping
public abstract class Identifiable {
    
    @Id(IDType.LOCAL)
    private String id;
    
    public String getId() {
        return id;
    }

    public String toString() {
        return id;
    }

}