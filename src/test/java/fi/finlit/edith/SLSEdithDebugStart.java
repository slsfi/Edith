/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith;

import java.io.File;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;

import com.mysema.commons.jetty.JettyHelper;

public class SLSEdithDebugStart {
    
    public static void main(String[] args) throws Exception{
        FSRepositoryFactory.setup();        
        File svnRepo = new File("target/repo-sls");
        
        System.setProperty("org.mortbay.jetty.webapp.parentLoaderPriority", "true");
        System.setProperty("production.mode", "false");
        System.setProperty(EDITH.REPO_FILE_PROPERTY, svnRepo.getAbsolutePath());
        System.setProperty(EDITH.REPO_URL_PROPERTY, SVNURL.fromFile(svnRepo).toString());
        System.setProperty(EDITH.RDFBEAN_DATA_DIR, "target/data-sls");
        
        System.setProperty(EDITH.EXTENDED_TERM, "true");
        
        JettyHelper.startJetty("src/main/webapp", "/", 8080, 8443);
    }

}
