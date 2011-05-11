package fi.finlit.edith.ui.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.mysema.commons.jetty.JettyConfig;
import com.mysema.commons.jetty.JettyHelper;
import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.testutil.SystemPropertyCheckRule;

public abstract class Selenium {

    private static WebDriver driver;

    public abstract WebappStarter starter();

    private static JettyConfig config;

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
            config = starter().start();
            driver = new FirefoxDriver(profile());
        }
    }
    
    @AfterClass
    public static void afterClass() {
        driver.close();
        driver = null;
        JettyHelper.stopJettyAtPort(config.port);
    }
    
    @Rule
    public MethodRule rule = new SystemPropertyCheckRule("webtest");

    public String path() {
        return "http://127.0.0.1:" + config.port;
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

}
