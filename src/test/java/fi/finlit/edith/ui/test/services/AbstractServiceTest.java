/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.test.services;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;

import com.mysema.rdfbean.tapestry.services.RDFBeanModule;

import fi.finlit.edith.EDITH;
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
@Modules({
    ServiceModule.class,
    ServiceTestModule.class,
    DataModule.class, 
    RDFBeanModule.class})
public abstract class AbstractServiceTest {

    @BeforeClass
    public static void beforeClass() throws SVNException{
        File svnRepo = new File("target/repo");
        FSRepositoryFactory.setup();
        System.setProperty(EDITH.REPO_FILE_PROPERTY, svnRepo.getAbsolutePath());
        System.setProperty(EDITH.REPO_URL_PROPERTY, SVNURL.fromFile(svnRepo).toString());
    }

}
