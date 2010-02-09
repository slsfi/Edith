package fi.finlit.edith.ui.web;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;

import com.mysema.commons.jetty.JettyHelper;

import fi.finlit.edith.EDITH;

public class CrawlingTest {
    private WebDriver webDriver;
    private String baseUrl = "http://localhost:8080";

    @BeforeClass
    public static void setUpClass() throws Exception {
        FSRepositoryFactory.setup();
        File svnRepo = new File("target/repo");

        System.setProperty("org.mortbay.jetty.webapp.parentLoaderPriority", "true");
        System.setProperty("production.mode", "false");
        System.setProperty(EDITH.REPO_FILE_PROPERTY, svnRepo.getAbsolutePath());
        System.setProperty(EDITH.REPO_URL_PROPERTY, SVNURL.fromFile(svnRepo).toString());
        JettyHelper.startJetty("src/main/webapp", "/", 8080, 8443);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        JettyHelper.stopJettyAtPort(8080);
    }

    @Before
    public void setUp() {
        webDriver = new HtmlUnitDriver();
    }

    @Test
    @Ignore
    public void crawlAnonymous() throws Exception {
        crawl();
    }

    @Test
    @Ignore
    public void crawlLoggedIn() throws Exception {
        webDriver.get(baseUrl + "/login");
        WebElement loginForm = webDriver.findElement(By.name("j_username"));
        loginForm.sendKeys("vesa");
        webDriver.findElement(By.name("j_password")).sendKeys("vesa");
        loginForm.submit();
        Set<String> visited = new HashSet<String>();
        visited.add("/logout");
        crawl(visited);
        webDriver.get(baseUrl + "/logout");
    }

    private void crawl() {
        crawl(new HashSet<String>());
    }

    private void crawl(Set<String> visited) {
        Stack<String> links = new Stack<String>();
        links.add("/");
        while (!links.isEmpty()) {
            String current = links.pop();
            current = current.startsWith("/") ? current : "/" + current;
            if (visited.contains(current)) {
                continue;
            }
            webDriver.get(baseUrl + current);
            visited.add(current);
            for (WebElement element : webDriver.findElements(By.tagName("a"))) {
                String href = null;
                try {
                    href = URLDecoder.decode(element.getAttribute("href"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                if (href == null || href.startsWith("mailto:") || href.startsWith("http")
                        || href.contains("#")) {
                    continue;
                } else {
                    links.add(href);
                }
            }
            System.out.println(webDriver.getCurrentUrl());
        }
    }

}
