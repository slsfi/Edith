/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

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

    /**
     * @return
     */
    Collection<Entity> getAll();

    /**
     * @param id
     * @return
     */
    Entity getById( Id id );

    /**
     * @param entity
     */
    void remove( Entity entity );
    
    void remove(Id id);

    /**
     * @param entity
     * @return
     */
    Entity save( Entity entity );
    
    void saveAll( Iterable<? extends Entity> entities);

}
