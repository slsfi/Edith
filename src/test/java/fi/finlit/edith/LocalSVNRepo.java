/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith;

import java.io.File;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;

/**
 * LocalSVNRepo provides
 *
 * @author tiwe
 * @version $Id$
 */
public class LocalSVNRepo {
    
    public static void init(File svnRepo) throws SVNException {
        if (!svnRepo.exists()){
            SVNURL repoURL = SVNRepositoryFactory.createLocalRepository(svnRepo, true , false );   
            
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

}
