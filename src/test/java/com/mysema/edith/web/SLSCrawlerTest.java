package com.mysema.edith.web;

import org.junit.Ignore;

import com.mysema.commons.jetty.WebappStarter;
import com.mysema.edith.Setups;

@Ignore
public class SLSCrawlerTest extends AbstractCrawlerTest {
    @Override
    public WebappStarter starter() {
        return Setups.SLS_TEST;
    }

}
