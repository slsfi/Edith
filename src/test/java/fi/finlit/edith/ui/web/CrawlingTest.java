package fi.finlit.edith.ui.web;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.mysema.commons.jetty.JettyHelper;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.testutil.SystemPropertyCheckRule;

public class CrawlingTest {

    private static final Logger logger = LoggerFactory.getLogger(CrawlingTest.class);

    // TODO : make sure resources (CSS etc) are accessible for BOTH anonymous and authenticated
    // users

    // TODO Empty page? Not redirected to login?

    private WebDriver webDriver;

    private String baseUrl = "http://localhost:9080";

    private static final String USERNAME = "vesa";
    private static final String PASSWORD = "vesa";

    private static boolean applicationStarted = false;

    @BeforeClass
    public static void setUpClass() throws Exception {
        if (System.getProperty("webtest") != null) {
            applicationStarted = true;
            FSRepositoryFactory.setup();
            File svnRepo = new File("target/repo");

            System.setProperty("org.mortbay.jetty.webapp.parentLoaderPriority", "true");
            System.setProperty("production.mode", "false");
            System.setProperty(EDITH.REPO_FILE_PROPERTY, svnRepo.getAbsolutePath());
            System.setProperty(EDITH.REPO_URL_PROPERTY, SVNURL.fromFile(svnRepo).toString());
            JettyHelper.startJetty("src/main/webapp", "/", 9080, 9443);
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (applicationStarted) {
            JettyHelper.stopJettyAtPort(9080);
        }
    }

    @Before
    public void setUp() {
        webDriver = new HtmlUnitDriver(){
            @Override
            protected WebClient modifyWebClient(WebClient client) {
                client.setThrowExceptionOnFailingStatusCode(true);
                client.setPrintContentOnFailingStatusCode(true);
                client.setJavaScriptEnabled(false);
                return client;
            }
        };
    }

    @Rule
    public MethodRule rule = new SystemPropertyCheckRule("webtest");

    @Test
    public void browsePages() throws Exception {
        webDriver.get(baseUrl + "/login");
        webDriver.findElement(By.name("j_username")).sendKeys(USERNAME);
        webDriver.findElement(By.name("j_password")).sendKeys(PASSWORD);
        webDriver.findElement(By.id("loginForm")).submit();
        Set<String> pages = crawl();
        webDriver.get(baseUrl + "/logout");
        for (String page : pages) {
            webDriver.get(baseUrl + page);
            String currentUrl = webDriver.getCurrentUrl();
            if (!currentUrl.contains("login") && !currentUrl.contains("about")) {
                fail(currentUrl + " should not be accessible!");
            }
        }
    }

    private Set<String> crawl() {
        Stack<String> links = new Stack<String>();
        Set<String> result = new HashSet<String>();
        Set<String> visited = new HashSet<String>();
        links.add("/");
        visited.add("/logout");
        while (!links.isEmpty()) {
            String current = links.pop();
            current = current.startsWith("/") ? current : "/" + current;
            if (visited.contains(current)) {
                continue;
            }
            result.add(current);
            webDriver.get(baseUrl + current);
            visited.add(current);
            logger.debug(webDriver.getCurrentUrl());
            if (webDriver.getTitle().contains("Exception")) {
                fail(webDriver.getCurrentUrl() + " contained an exception!");
            }
            for (WebElement element : webDriver.findElements(By.tagName("a"))) {
                String href = null;
                try {
                    href = URLDecoder.decode(element.getAttribute("href"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                if (href != null && !href.startsWith("mailto:") && !href.startsWith("http")
                        && !href.contains("#")) {
                    links.add(href);
                }
            }
        }
        return result;
    }

}
