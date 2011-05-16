package fi.finlit.edith;

import com.mysema.commons.jetty.WebappStarter;


public final class Setups {

    public static final WebappStarter SKS_DEV = new EdithDebugStart("target/sks-dev/", 8080);
    
    public static final WebappStarter SKS_TEST = new EdithDebugStart("target/sks-test/", 8091);
    
    public static final WebappStarter SLS_DEV = new SLSEdithDebugStart("target/sls-dev/", 8080);
    
    public static final WebappStarter SLS_TEST = new SLSEdithDebugStart("target/sls-test/", 8090);
    
    private Setups() {}
}
