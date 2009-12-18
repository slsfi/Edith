/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.editio.domain;

import java.io.Serializable;
import java.util.Collection;

/**
 * Repository provides
 *
 * @author tiwe
 * @version $Id$
 *
 */
public interface Repository<Entity, Id extends Serializable> {    

    Collection<Entity> getAll();

    Entity getById( Id id );

    void remove( Entity entity );

    Entity save( Entity entity );

}
