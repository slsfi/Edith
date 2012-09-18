package com.mysema.edith;

import com.mysema.commons.jetty.WebappStarter;


public final class Setups {

    public static final WebappStarter SKS_DEV = new EdithDebugStart("target/sks-dev/", 8080, false);
    
    public static final WebappStarter SKS_TEST = new EdithDebugStart("target/sks-test/", 8090, true);
    
    public static final WebappStarter SLS_DEV = new SLSEdithDebugStart("target/sls-dev/", 8080, false);
    
    public static final WebappStarter SLS_TEST = new SLSEdithDebugStart("target/sls-test/", 8090, true);
    
    private Setups() {}
}
