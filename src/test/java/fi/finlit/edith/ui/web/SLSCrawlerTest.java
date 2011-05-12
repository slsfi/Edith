package fi.finlit.edith.ui.web;

import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.SLSEdithDebugStart;

public class SLSCrawlerTest extends AbstractCrawlerTest {

    @Override
    public WebappStarter starter() {
        return new SLSEdithDebugStart("target/sls-test/", 8090);
    }

}