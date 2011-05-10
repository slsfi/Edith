package fi.finlit.edith.ui.web;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.SLSEdithDebugStart;


public class SLSAnnotateTest extends Selenium {
    
    @Before
    public void before() {
        startSelenium();
    }
    

    @Test
    public void testSvLocale() {
        
        get("/login");
        findElement(By.name("j_username")).sendKeys("lassi");
        WebElement pass = findElement(By.name("j_password"));
        pass.sendKeys("lassi");
        pass.submit();
        
        assertTitle("Dokument");
        assertLink("Kommentarer");
        assertLink("Hämta kommentarer");
        assertLink("Instructioner");
        
        //Open nummisuutarit_simp.xml
        findElement("a[href='/document/annotate/12']").click();
        assertTitleNot("Application Exception");
        
        
        
        
        
    }
    
    
    @Override
    public String locales() {
        return "sv,fi,en";
    }
    
    @Override
    public WebappStarter starter() {
        return new SLSEdithDebugStart();
    }

    @Override
    public int port() {
        return 8090;
    }

}
