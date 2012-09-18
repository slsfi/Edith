package com.mysema.edith.services;

import java.io.Serializable;

/**
 * @author tiwe
 * 
 * @param <Entity>
 * @param <Id>
 */
public interface Dao<Entity, Id extends Serializable> {
    /**
     * Get the persisted instance with the given id
     * 
     * @param id
     * @return
     */
    Entity getById(Id id);

}