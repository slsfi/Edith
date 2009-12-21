/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith;

import java.io.File;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import com.mysema.commons.jetty.JettyHelper;

public class EdithDebugStart {
    
    private static final File svnRepo = new File("target/repo");
    
    public static void main(String[] args) throws Exception{
        createRepository();
        
        System.setProperty("org.mortbay.jetty.webapp.parentLoaderPriority", "true");
        System.setProperty("production.mode", "false");
        System.setProperty("svn.repo", "svn://" + svnRepo.getAbsolutePath());
        JettyHelper.startJetty("src/main/webapp", "/", 8080, 8443);
    }

    private static void createRepository() throws SVNException {
        if (!svnRepo.exists()){
            SVNURL url = SVNRepositoryFactory.createLocalRepository(svnRepo, true , false );   
            SVNRepository repository = SVNRepositoryFactory.create( url, null );
            repository.closeSession();
        }        
    }

}
