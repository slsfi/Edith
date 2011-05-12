package fi.finlit.edith.ui.web;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.SLSEdithDebugStart;

public class SLSAnnotateTest extends AbstractSeleniumTest {

    @Test
    public void Texts_Are_In_Swedish_Locale() {

        get("/login");
        findElement(By.name("j_username")).sendKeys("lassi");
        WebElement pass = findElement(By.name("j_password"));
        pass.sendKeys("lassi");
        pass.submit();

        assertTitle("Dokument");
        assertLink("Kommentarer");
        assertLink("Hämta kommentarer");
        assertLink("Instructioner");

        // Open nummisuutarit_simp.xml
        findElement("a[href='/document/annotate/12']").click();
        assertTitleNot("Application Exception");

        assertContainsText(findElement(By.id("document_links")), "Kommentarvy");
        assertLink("Textvy");
        assertLink("Publiceringsvy");
    }

    @Test
    public void New_Note_Can_Be_Created() {
        
        
        
        
    }

    @Override
    public String locales() {
        return "sv,fi,en";
    }

    @Override
    public WebappStarter starter() {
        return SLSEdithDebugStart.test();
    }

}
