/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.hibernate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tapestry5.hibernate.HibernateCoreModule;
import org.junit.runner.RunWith;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;

import fi.finlit.edith.testutil.Modules;
import fi.finlit.edith.testutil.TapestryTestRunner;
import fi.finlit.edith.ui.services.HibernateDataModule;
import fi.finlit.edith.ui.services.HibernateServiceModule;
import fi.finlit.edith.ui.test.services.SKSServiceTestModule;

@RunWith(TapestryTestRunner.class)
@Modules({ SKSServiceTestModule.class, HibernateServiceModule.class, HibernateDataModule.class, HibernateCoreModule.class })
public abstract class AbstractHibernateTest {
    static {
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

    protected static final String start(String localId) {
        return "<anchor xml:id=\"start" + localId + "\"/>";
    }

    protected static final String end(String localId) {
        return "<anchor xml:id=\"end" + localId + "\"/>";
    }

}
