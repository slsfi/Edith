package fi.finlit.edith.ui.services.repository;

import java.util.Collection;

import com.mysema.query.types.EntityPath;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.ui.services.AbstractService;
import fi.finlit.edith.ui.services.Dao;

/**
 * AbstractRepository provides a basic stub for Repository implementations
 *
 * @author tiwe
 * @version $Id$
 */
public abstract class AbstractRepository<T> extends AbstractService
    implements Dao<T,String>{

    private final EntityPath<T> entityPath;

    protected AbstractRepository(SessionFactory sessionFactory, EntityPath<T> entity){
        super(sessionFactory);
        this.entityPath = entity;
    }

    @SuppressWarnings("unchecked")
    protected Class<T> getType(){
        return (Class<T>) entityPath.getType();
    }

    @Override
    public Collection<T> getAll() {
        return getSession().findInstances(getType());
    }

    @Override
    public T getById(String id) {
        return getSession().getById(id, entityPath.getType());
    }

}