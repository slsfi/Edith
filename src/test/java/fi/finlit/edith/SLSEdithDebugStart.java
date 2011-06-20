/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;

import com.mysema.commons.jetty.JettyConfig;
import com.mysema.commons.jetty.WebappStarter;

public final class SLSEdithDebugStart extends WebappStarter {

    public static void main(String[] args) throws Exception {
        Setups.SLS_DEV.start();
    }

    private final String root;

    private final int port;

    private final boolean clear;

    SLSEdithDebugStart(String root, int port, boolean clear) {
        this.root = root;
        this.port = port;
        this.clear = clear;
    }

    @Override
    public JettyConfig configure() throws Exception {
        FSRepositoryFactory.setup();
        File rootFile = new File(root);
        if (clear && rootFile.exists()) {
            FileUtils.cleanDirectory(rootFile);
            rootFile.delete();
            File svnCache = new File("target/svncache");
            if (svnCache.exists()) {
                FileUtils.cleanDirectory(svnCache);
                svnCache.delete();
            }
        }
        File svnRepo = new File(rootFile, "repo-sls");

        System.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/edith?useUnicode=true&characterEncoding=UTF-8");
        System.setProperty("org.mortbay.jetty.webapp.parentLoaderPriority", "true");
        System.setProperty("production.mode", "false");
        System.setProperty(EDITH.REPO_FILE_PROPERTY, svnRepo.getAbsolutePath());
        System.setProperty(EDITH.REPO_URL_PROPERTY, SVNURL.fromFile(svnRepo).toString());
        System.setProperty(EDITH.RDFBEAN_DATA_DIR, root + "data-sls");

        System.setProperty(EDITH.EXTENDED_TERM, "true");

        return new JettyConfig(port);
    }

}
