/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.test.services;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;

/**
 * SVNTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class SVNTest {
    
    @Test
    public void test() throws SVNException, IOException{
        // register support for "file" protocol based SVN usage
        FSRepositoryFactory.setup();
        
        // create repo
        File svnRepo = new File("target/SVNTest-repo");
        FileUtils.deleteDirectory(svnRepo);
        SVNURL repoURL = SVNRepositoryFactory.createLocalRepository(svnRepo, true , false );
        System.out.println(repoURL);
                
        SVNClientManager clientManager = SVNClientManager.newInstance();
        SVNCommitClient commitClient = clientManager.getCommitClient();
        
        // mkdir
        commitClient.doMkDir(new SVNURL[]{
                repoURL.appendPath("documents", false),
                repoURL.appendPath("documents/trunk", false)
        }, "created initial folders");
        
        // imports
        SVNURL folder = repoURL.appendPath("documents/trunk", false);
        for (File file : new File("etc/demo-material/tei").listFiles()){
            if (file.isFile()){
                commitClient.doImport(file, folder.appendPath(file.getName(), false), file.getName() + " added", false);    
            }            
        }
        
    }
}
