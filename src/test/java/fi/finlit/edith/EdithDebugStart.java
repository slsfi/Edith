/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith;

import java.io.File;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;

import com.mysema.commons.jetty.JettyConfig;
import com.mysema.commons.jetty.JettyHelper;
import com.mysema.commons.jetty.WebappStarter;

public class EdithDebugStart implements WebappStarter {
    
    public static void main(String[] args) throws Exception{
        new EdithDebugStart().start(8080);
    }

    @Override
    public JettyConfig start(int port) throws Exception {
        FSRepositoryFactory.setup();        
        File svnRepo = new File("target/repo");
        
        System.setProperty("org.mortbay.jetty.webapp.parentLoaderPriority", "true");
        System.setProperty("production.mode", "false");
        System.setProperty(EDITH.REPO_FILE_PROPERTY, svnRepo.getAbsolutePath());
        System.setProperty(EDITH.REPO_URL_PROPERTY, SVNURL.fromFile(svnRepo).toString());
        
        System.setProperty(EDITH.EXTENDED_TERM, "false");
        
        return JettyHelper.startJetty("src/main/webapp", "/", port, port+1);
    }

}
