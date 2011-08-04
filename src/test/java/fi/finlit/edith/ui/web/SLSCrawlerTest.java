package fi.finlit.edith.ui.web;

import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.Setups;

public class SLSCrawlerTest extends AbstractCrawlerTest {
    @Override
    public WebappStarter starter() {
        return Setups.SLS_TEST;
    }

}
