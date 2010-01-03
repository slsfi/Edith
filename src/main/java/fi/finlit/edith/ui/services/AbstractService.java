/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionFactory;
import com.mysema.rdfbean.query.PagedBeanQuery;

/**
 * AbstractService provides
 *
 * @author tiwe
 * @version $Id$
 */
public abstract class AbstractService {
    
    @Inject
    private SessionFactory sessionFactory;
    
    protected Session getSession(){
        return sessionFactory.getCurrentSession();
    }    

    protected PagedBeanQuery getPagedQuery(){
        return new PagedBeanQuery(sessionFactory);
    }
}
