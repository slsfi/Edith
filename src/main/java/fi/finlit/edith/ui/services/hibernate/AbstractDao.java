package fi.finlit.edith.ui.services.hibernate;

import java.util.Collection;

import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.mysema.query.jpa.hibernate.HibernateSubQuery;

import fi.finlit.edith.ui.services.Dao;

public abstract class AbstractDao<T> implements Dao<T, Long> {
    @Inject
    private HibernateSessionManager sessionManager;

    protected JPQLQuery query() {
        return new HibernateQuery(getSession());
    }

    protected Session getSession() {
        return sessionManager.getSession();
    }
    
    protected HibernateSubQuery sub() {
        return new HibernateSubQuery();
    }
    
}