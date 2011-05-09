package fi.finlit.edith.ui.test.pages;

import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import fi.finlit.edith.domain.User;
import fi.finlit.edith.ui.pages.Register;
import fi.finlit.edith.ui.test.services.AbstractServiceTest;

public class RegisterTest extends AbstractServiceTest {

    @Autobuild
    @Inject
    private Register registerPage;

    @Test
    public void OnSuccess(){
        User user = new User();
        registerPage.setUser(user);
        registerPage.onSuccess();
    }

}
