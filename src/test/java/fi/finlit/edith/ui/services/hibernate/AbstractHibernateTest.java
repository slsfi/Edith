/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.hibernate;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tapestry5.hibernate.HibernateCoreModule;
import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.hibernate.mapping.Table;
import org.junit.After;
import org.junit.runner.RunWith;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;

import fi.finlit.edith.testutil.Modules;
import fi.finlit.edith.testutil.TapestryTestRunner;
import fi.finlit.edith.ui.config.HibernateServiceModule;
import fi.finlit.edith.ui.config.ServiceModule;
import fi.finlit.edith.ui.services.SKSServiceTestModule;

@RunWith(TapestryTestRunner.class)
@Modules({ SKSServiceTestModule.class, ServiceModule.class, HibernateServiceModule.class,
        HibernateCoreModule.class })
public abstract class AbstractHibernateTest {
    @Inject
    private HibernateSessionManager sessionManager;

    private static String[] truncateAllTablesSql;

    @Inject
    private HibernateSessionSource source;

    static {
//        System.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/edith_test?useUnicode=true&characterEncoding=UTF-8");
        FSRepositoryFactory.setup();
    }

    private final List<InputStream> openStreams = new ArrayList<InputStream>();

    protected static final XMLInputFactory inFactory = XMLInputFactory.newInstance();

    protected static final XMLOutputFactory outFactory = XMLOutputFactory.newInstance();

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

    protected Session getSession() {
        return sessionManager.getSession();
    }

    protected JPQLQuery query() {
        return new HibernateQuery(getSession());
    }

    @After
    public final void clearTestData() {
        // Clear session test method used
        TapestryTestRunner.getRegistry(getClass()).cleanupThread();
        // And start a new session to clear the database
        getSession().doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                Statement stmt = connection.createStatement();
                stmt.execute("set foreign_key_checks=0");
                for (String sql : getTruncateAllTablesSql()) {
                    stmt.execute(sql);
                }
                stmt.execute("set foreign_key_checks=1");

            }
        });
        sessionManager.commit();
        TapestryTestRunner.getRegistry(getClass()).cleanupThread();
    }

    private String[] getTruncateAllTablesSql() {
        if (truncateAllTablesSql != null) {
            return truncateAllTablesSql;
        }
        List<String> sql = new ArrayList<String>();
        for (Iterator<Table> iter = source.getConfiguration().getTableMappings(); iter.hasNext();) {
            Table table = iter.next();
            sql.add("truncate " + table.getName());
        }
        return sql.toArray(new String[sql.size()]);
    }

}
