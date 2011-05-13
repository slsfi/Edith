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
import com.mysema.commons.jetty.WebappStarter;

public final class SLSEdithDebugStart extends WebappStarter {

    public static WebappStarter dev() {
        return new SLSEdithDebugStart("target/sls-dev/", 8080);
    }
    
    public static WebappStarter test() {
        return new SLSEdithDebugStart("target/sls-test/", 8090);
    }
        
    public static void main(String[] args) throws Exception {
        SLSEdithDebugStart.dev().start();
    }

    private final String root;
    
    private final int port;
    
    public SLSEdithDebugStart(String root, int port) {
        this.root = root;
        this.port = port;
    }

    @Override
    public JettyConfig configure() throws Exception {
        FSRepositoryFactory.setup();
        File svnRepo = new File(root + "repo-sls");

        System.setProperty("org.mortbay.jetty.webapp.parentLoaderPriority", "true");
        System.setProperty("production.mode", "false");
        System.setProperty(EDITH.REPO_FILE_PROPERTY, svnRepo.getAbsolutePath());
        System.setProperty(EDITH.REPO_URL_PROPERTY, SVNURL.fromFile(svnRepo).toString());
        System.setProperty(EDITH.RDFBEAN_DATA_DIR, root + "data-sls");

        System.setProperty(EDITH.EXTENDED_TERM, "true");
        
        return new JettyConfig(port);
    }

}
