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

import fi.finlit.edith.EDITH;
import fi.finlit.edith.LocalSVNRepo;
import fi.finlit.edith.testutil.Modules;
import fi.finlit.edith.testutil.TapestryTestRunner;
import fi.finlit.edith.ui.services.ServiceModule;

/**
 * AbstractServiceTest provides
 *
 * @author tiwe
 * @version $Id$
 */
@RunWith(TapestryTestRunner.class)
@Modules(ServiceModule.class)
public abstract class AbstractServiceTest {

    @BeforeClass
    public static void beforeClass() throws SVNException{
        File svnRepo = new File("target/" + System.currentTimeMillis());
        FSRepositoryFactory.setup();
        LocalSVNRepo.init(svnRepo);
        System.setProperty(EDITH.REPO_FILE_PROPERTY, svnRepo.getAbsolutePath());
        System.setProperty(EDITH.REPO_URL_PROPERTY, SVNURL.fromFile(svnRepo).toString());
    }

}
