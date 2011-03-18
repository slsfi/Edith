package fi.finlit.edith.ui.test.pages;

import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import fi.finlit.edith.domain.User;
import fi.finlit.edith.ui.pages.RegisterPage;
import fi.finlit.edith.ui.test.services.AbstractServiceTest;

public class RegisterPageTest extends AbstractServiceTest {

    @Autobuild
    @Inject
    private RegisterPage registerPage;

    @Test
    public void OnSuccess(){
        User user = new User();
        registerPage.setUser(user);
        registerPage.onSuccess();
    }

}
