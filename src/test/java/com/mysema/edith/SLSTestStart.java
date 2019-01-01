/*
 * Copyright (c) 2018 Mysema
 */

/**
 * 
 */
package com.mysema.edith;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;

import com.mysema.commons.jetty.JettyHelper;


public class SLSTestStart {
    
    public static void main(String[] args) throws Exception {
        String root = "target/sls-test";
        FSRepositoryFactory.setup();
        File rootFile = new File(root);
        if (rootFile.exists()) {
            FileUtils.cleanDirectory(rootFile);
            rootFile.delete();
            File svnCache = new File("target/svncache");
            if (svnCache.exists()) {
                FileUtils.cleanDirectory(svnCache);
                svnCache.delete();
            }
        }
        File svnRepo = new File(rootFile, "repo-sls");

        System.setProperty("hibernate.connection.url", 
                "jdbc:mysql://localhost:3306/edith?useUnicode=true&characterEncoding=UTF-8");
        System.setProperty("org.mortbay.jetty.webapp.parentLoaderPriority", "true");
        System.setProperty("production.mode", "false");
        System.setProperty(EDITH.REPO_FILE_PROPERTY, svnRepo.getAbsolutePath());
        System.setProperty(EDITH.REPO_URL_PROPERTY, SVNURL.fromFile(svnRepo).toString());
        System.setProperty(EDITH.EXTENDED_TERM, "true");
        
        JettyHelper.startJetty("src/main/webapp", "/", 8080, 8443);
    }
    
}