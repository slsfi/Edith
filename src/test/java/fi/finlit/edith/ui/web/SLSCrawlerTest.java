package fi.finlit.edith.ui.web;

import com.mysema.commons.jetty.JettyConfig;
import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.SLSEdithDebugStart;

public class SLSCrawlerTest extends Crawler {

    @Override
    public WebappStarter starter() {
        return new SLSEdithDebugStart() {
            @Override
            public JettyConfig configure() throws Exception {
                root = "target/sls-test/";
                return super.configure().setPort(8090);
            }
        };
    }

}
