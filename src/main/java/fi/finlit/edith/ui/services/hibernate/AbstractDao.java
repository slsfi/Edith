package fi.finlit.edith.ui.services.hibernate;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;

import fi.finlit.edith.ui.services.Dao;
import fi.finlit.edith.util.JPQLGridDataSource;

public abstract class AbstractDao<T> implements Dao<T, Long> {
    @Inject
    private HibernateSessionManager sessionManager;

    protected JPQLQuery query() {
        return new HibernateQuery(getSession());
    }

    protected Session getSession() {
        return sessionManager.getSession();
    }

    protected HibernateSessionManager getSessionManager() {
        return sessionManager;
    }

    protected <K> GridDataSource createGridDataSource(final EntityPath<K> path,
            final OrderSpecifier<?> order, final boolean caseSensitive, final Predicate filters) {
        return new JPQLGridDataSource<K>(getSessionManager(), path, order, caseSensitive, filters);
    }

}