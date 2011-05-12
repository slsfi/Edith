package fi.finlit.edith.ui.web;

import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.EdithDebugStart;

public class SKSCrawlerTest extends AbstractCrawlerTest{
    
    @Override
    public WebappStarter starter() {
        return new EdithDebugStart("target/sks-test/", 8091);
    }

}
