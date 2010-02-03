/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Id;
import com.mysema.rdfbean.model.IDType;

/**
 * Identifiable provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping
public class Identifiable {
        
    @Id(IDType.LOCAL)
    private String id;
   
    public String getId() {
        return id;
    }

}
