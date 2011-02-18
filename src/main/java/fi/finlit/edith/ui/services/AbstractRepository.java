package fi.finlit.edith.ui.services;

import java.util.Collection;

import com.mysema.query.types.EntityPath;
import com.mysema.rdfbean.model.BID;
import com.mysema.rdfbean.model.IDType;
import com.mysema.rdfbean.model.UID;
import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionFactory;

/**
 * AbstractRepository provides a basic stub for Repository implementations
 *
 * @author tiwe
 * @version $Id$
 */
public abstract class AbstractRepository<T> extends AbstractService
    implements Repository<T,String>{

    private final EntityPath<T> entityPath;

    private final IDType idType;

    protected AbstractRepository(SessionFactory sessionFactory, EntityPath<T> entity){
        this(sessionFactory, entity, IDType.LOCAL);
    }

    protected AbstractRepository(SessionFactory sessionFactory, EntityPath<T> entity, IDType idType){
        super(sessionFactory);
        this.entityPath = entity;
        this.idType = idType;
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
        Session session = getSession();
        if (idType == IDType.LOCAL){
            return session.getById(id, getType());
        }else if (idType == IDType.RESOURCE){
            return session.get(entityPath.getType(), new BID(id));
        }else{
            return session.get(entityPath.getType(), new UID(id));
        }

    }

}