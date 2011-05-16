package fi.finlit.edith.ui.web;

import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.Setups;

public class SKSCrawlerTest extends AbstractCrawlerTest{
    
    @Override
    public WebappStarter starter() {
        return Setups.SKS_TEST;
    }

}
