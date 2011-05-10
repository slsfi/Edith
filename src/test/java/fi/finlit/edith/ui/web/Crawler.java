/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.web;

import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Crawler extends Selenium  {

    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);


    private static final String USERNAME = "vesa";
    private static final String PASSWORD = "vesa";

    @Test
    public void BrowsePages() throws Exception {
        get("/login");
        findElement(By.name("j_username")).sendKeys(USERNAME);
        findElement(By.name("j_password")).sendKeys(PASSWORD);
        findElement(By.id("loginForm")).submit();
        Set<String> pages = crawl();
        get("/logout");
        for (String page : pages) {
            get(page);
            String currentUrl = currentUrl();
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
            // FIXME We are skipping URLs that contain ".." because they break in HtmlUnit.
            // Find out why such URLs are constructed.
            if (visited.contains(current) || current.contains("..")) {
                continue;
            }
            result.add(current);
            driver.get(current);
            visited.add(current);
            logger.debug(driver.getCurrentUrl());
            if (title().contains("Exception")) {
                fail(currentUrl() + " contained an exception!");
            }
            for (WebElement element : findElements(By.tagName("a"))) {
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
