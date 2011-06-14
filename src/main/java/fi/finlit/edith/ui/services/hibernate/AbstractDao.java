package fi.finlit.edith.ui.services.hibernate;

import java.util.Collection;

import com.mysema.query.types.EntityPath;
import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.ui.services.AbstractService;
import fi.finlit.edith.ui.services.Dao;

/**
 * AbstractRepository provides a basic stub for Repository implementations
 * 
 * @author tiwe
 * @version $Id$
 */
public abstract class AbstractDao<T> implements Dao<T, String> {


    protected Session getSession() {return null;}

    @SuppressWarnings("unchecked")
    protected Class<T> getType() {
//        return (Class<T>) entityPath.getType();
        return null;
    }

    @Override
    public Collection<T> getAll() {
        return getSession().findInstances(getType());
    }

    @Override
    public T getById(String id) {
  //      return getSession().getById(id, entityPath.getType());
        return null;
    }

}