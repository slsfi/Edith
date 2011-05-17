package fi.finlit.edith.ui.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.ConnectException;
import java.net.URL;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.openqa.selenium.By;
import org.openqa.selenium.Mouse;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.DoubleClickAction;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mysema.commons.jetty.JettyConfig;
import com.mysema.commons.jetty.JettyHelper;
import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.testutil.SystemPropertyCheckRule;

public abstract class AbstractSeleniumTest {

    private static WebDriver driver;

    public abstract WebappStarter starter();

    private static JettyConfig config;
    
    private static boolean externalJetty = false;

    public String locales() {
        return "fi,sv,en";
    }

    public FirefoxProfile profile() {
        FirefoxProfile p = new FirefoxProfile();
        p.setPreference("intl.accept_languages", locales());
        return p;
    }

    private boolean webtestMode() {
        return System.getProperty("webtest") != null;
    }

    @Before
    public void beforeClass() throws Exception {
        if (webtestMode() && driver == null) {
            long start = System.currentTimeMillis();
            driver = new FirefoxDriver(profile());
            System.out.println("Firefox driver start took " + (System.currentTimeMillis() - start));

            config = starter().configure();
            
            //Try if there is jetty running on our port already
            try {
                new URL(path() + "/").getContent();
                System.out.println("Jetty already running on port " + config.port);
                externalJetty = true;
                return;
            } catch(ConnectException e) {
                //No jetty, start it then
            }
            
            starter().start();
            
        }
    }
    
    @AfterClass
    public static void afterClass() {
        if (driver != null) {
            driver.close();
            driver = null;
            
            if (!externalJetty) {
                JettyHelper.stopJettyAtPort(config.port);
            }
        }       
        
    }
    
    @Rule
    public MethodRule rule = new SystemPropertyCheckRule("webtest");

    public String path() {
        return "http://127.0.0.1:" + config.port;
    }
    

    protected void login(String user, String password) {
        get("/login");
        if (!title().contains("Access is denied")) {
            findElement(By.name("j_username")).sendKeys(user);
            WebElement passwordElement = findElement(By.name("j_password"));
            passwordElement.sendKeys(password);
            passwordElement.submit();    
        }
        
    }
    
    protected void dblClick(WebElement element) {
        Action dblClick = new DoubleClickAction(getMouse(), (Locatable)element);
        dblClick.perform();
    }

    private Mouse getMouse() {
        return ((FirefoxDriver)driver).getMouse();
    }
    
    public void get(String url) {
        driver.get(path() + url);
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public String title() {
        return driver.getTitle();
    }
    
    public String pageSource() {
        return driver.getPageSource();
    }

    public WebElement findByLinkText(String linkText) {
        return driver.findElement(By.linkText(linkText));
    }

    public WebElement findElement(By by) {
        return driver.findElement(by);
    }

    public WebElement findElement(String cssSelector) {
        return driver.findElement(byCss(cssSelector));
    }

    public List<WebElement> findElements(By by) {
        return driver.findElements(by);
    }

    public List<WebElement> findElements(String cssSelector) {
        return driver.findElements(byCss(cssSelector));
    }

    public By byCss(String cssSelector) {
        return By.cssSelector(cssSelector);
    }

    public void assertTitle(String title) {
        assertEquals("Page title", title, driver.getTitle());
    }

    public void assertTitleNot(String title) {
        assertTrue("Page title should not be: " + title, !title.equals(driver.getTitle()));
    }

    public void assertLink(String linkText) {
        assertNotNull("Link text not found: " + linkText, findByLinkText(linkText));
    }
    
    protected void wait(ExpectedCondition<Boolean> expected) {
        Wait<WebDriver> wait = new WebDriverWait(driver, 3);
        wait.until(expected);
    }
    
    protected ExpectedCondition<Boolean> element(final By findCondition) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver from) {
                RenderedWebElement element = (RenderedWebElement)driver.findElement(findCondition);
                return element.isDisplayed();
            }
        };
    }

    public void assertContainsText(WebElement element, String expected) {
        assertNotNull(element);
        assertTrue("Element [" + element.getText() + "] does not contain text [" + expected + "]",
                element.getText().contains(expected));

    }

}
