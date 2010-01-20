/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;

import fi.finlit.edith.EDITH;

/**
 * Person provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITH.NS)
public class Person extends Identifiable{
    
    // firstName, lastName, dateOfBirth, dateOfDeath etc

}
