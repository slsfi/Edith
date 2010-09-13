/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.junit.runner.RunWith;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;

import com.mysema.rdfbean.tapestry.services.RDFBeanModule;

import fi.finlit.edith.testutil.Modules;
import fi.finlit.edith.testutil.TapestryTestRunner;
import fi.finlit.edith.ui.services.DataModule;
import fi.finlit.edith.ui.services.ServiceModule;

/**
 * AbstractServiceTest provides
 *
 * @author tiwe
 * @version $Id$
 */
@RunWith(TapestryTestRunner.class)
@Modules({ ServiceTestModule.class, ServiceModule.class, DataModule.class, RDFBeanModule.class })
public abstract class AbstractServiceTest {

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
