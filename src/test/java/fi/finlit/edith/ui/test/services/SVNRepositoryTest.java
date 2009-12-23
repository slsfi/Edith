/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.test.services;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * SVNRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class SVNRepositoryTest extends AbstractServiceTest{

    @Inject
    private SVNRepository svnRepository;
    
    @SuppressWarnings("unchecked")
    @Test
    public void test() throws SVNException{
        Collection entries = new ArrayList();
        long revision = svnRepository.getLatestRevision();
        System.err.println(revision);
        svnRepository.getDir("/documents/trunk", revision, false, entries);
        for (Object entry : entries){
            System.err.println(entry.getClass().getSimpleName() + " : " + entry);
        }
    }

}
