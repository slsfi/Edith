package fi.finlit.edith.ui.test.pages;

import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import fi.finlit.edith.ui.pages.LogoutPage;
import fi.finlit.edith.ui.test.services.AbstractServiceTest;

public class LogoutPageTest extends AbstractServiceTest{

    @Autobuild
    @Inject
    private LogoutPage logoutPage;
    
    @Test
    public void onActivate(){
        logoutPage.onActivate();
        // TODO : assertions
    }
    
}
