/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.editio.ui.services;

import java.util.Collection;

import com.mysema.query.types.path.PEntity;

import fi.finlit.editio.domain.Repository;

/**
 * AbstractRepository provides
 *
 * @author tiwe
 * @version $Id$
 */
public abstract class AbstractRepository<T> extends AbstractService 
    implements Repository<T,String>{
    
    private final PEntity<T> entity;
    
    protected AbstractRepository(PEntity<T> entity){
        this.entity = entity;
    }
    
    @SuppressWarnings("unchecked")
    protected Class<T> getType(){
        return (Class<T>) entity.getType();
    }
    
    @Override
    public Collection<T> getAll() {
        return getSession().findInstances(getType());
    }

    @Override
    public T getById(String id) {
        return getSession().getById(id, getType());
    }

    @Override
    public void remove(T entity) {
        getSession().delete(entity);        
    }

    @Override
    public T save(T entity) {
        getSession().save(entity);
        return entity;
    }
    
}
