/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith;

import java.io.File;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;

import com.mysema.commons.jetty.JettyHelper;

public class EdithDebugStart {
    
    private static final File svnRepo = new File("target/repo");
    
    public static void main(String[] args) throws Exception{
        FSRepositoryFactory.setup();
        createRepository();
        
        System.setProperty("org.mortbay.jetty.webapp.parentLoaderPriority", "true");
        System.setProperty("production.mode", "false");
        System.setProperty(EDITH.REPO_FILE_PROPERTY, svnRepo.getAbsolutePath());
        System.setProperty(EDITH.REPO_URL_PROPERTY, SVNURL.fromFile(svnRepo).toString());
        JettyHelper.startJetty("src/main/webapp", "/", 8080, 8443);
    }

    @SuppressWarnings("deprecation")
    private static void createRepository() throws SVNException {
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
