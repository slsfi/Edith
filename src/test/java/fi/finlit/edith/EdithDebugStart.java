/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith;

import com.mysema.commons.jetty.JettyHelper;

public class EdithDebugStart {
    
    public static void main(String[] args) throws Exception{
        System.setProperty("org.mortbay.jetty.webapp.parentLoaderPriority", "true");
        System.setProperty("production.mode", "false");
        JettyHelper.startJetty("src/main/webapp", "/", 8080, 8443);
    }

}
