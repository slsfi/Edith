/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.junit.After;
import org.junit.runner.RunWith;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.mysema.edith.guice.GuiceTestRunner;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;

@RunWith(GuiceTestRunner.class)
public abstract class AbstractHibernateTest {

    static {
        FSRepositoryFactory.setup();
    }
    
    protected static final XMLInputFactory inFactory = XMLInputFactory.newInstance();

    protected static final XMLOutputFactory outFactory = XMLOutputFactory.newInstance();

    private static final List<String> tables = new ArrayList<String>();
    
    @Inject
    private Provider<EntityManager> em;
    
    protected EntityManager getEntityManager() {
        return em.get();
    }

    private final List<InputStream> openStreams = new ArrayList<InputStream>();

    
    protected InputStream register(InputStream is) {
        openStreams.add(is);
        return is;
    }

    protected void closeStreams() {
        for (InputStream is : openStreams) {
            try {
                is.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    protected static final String start(Long localId) {
        return "<anchor xml:id=\"start" + localId + "\"/>";
    }

    protected static final String end(Long localId) {
        return "<anchor xml:id=\"end" + localId + "\"/>";
    }

    protected JPQLQuery query() {
        return new JPAQuery(em.get());
    }

    @After
    @Transactional
    public void clearTestData() {
        // clear cache
        Cache cache = em.get().getEntityManagerFactory().getCache();
        cache.evictAll();
        
        Session session = em.get().unwrap(Session.class);
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                if (tables.isEmpty()) {
                    DatabaseMetaData md = connection.getMetaData();
                    ResultSet rs = md.getTables(null, null, null, null);
                    try {
                        while (rs.next()) {
                            tables.add(rs.getString("TABLE_NAME"));
                        }
                    } finally {
                        rs.close();
                    }    
                }                                
                
                // truncate
                Statement stmt = connection.createStatement();
                stmt.execute("set foreign_key_checks=0");
                for (String table : tables) {
                    if (!table.toLowerCase().equals("user")) {
                        stmt.execute("truncate " + table);    
                    }                    
                }
                stmt.execute("set foreign_key_checks=1");

            }
        });
    }

}
