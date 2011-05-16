package fi.finlit.edith.ui.web;

import org.junit.Test;
import org.openqa.selenium.By;

import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.Setups;

public class SLSAnnotateTest extends AbstractSeleniumTest {

    @Test
    public void Documents_Texts_Are_In_Swedish_Locale() {
        login("lassi", "lassi");
        get("/documents");
        
        assertTitle("Dokument");
        assertLink("Kommentarer");
        assertLink("Hämta kommentarer");
        assertLink("Instructioner");
    }
    
    @Test
    public void Annotate_Texts_Are_In_Swedish_Locale() {
        login("lassi", "lassi");
        get("/documents");
        findElement("a[href='/document/annotate/12']").click();

        assertTitleNot("Application Exception");
        assertContainsText(findElement(By.id("document_links")), "Kommentarvy");
        assertLink("Textvy");
        assertLink("Publiceringsvy");
    }

    @Test
    public void New_Note_Can_Be_Created() {
        // TODO
        
    }

    @Override
    public String locales() {
        return "sv,fi,en";
    }

    @Override
    public WebappStarter starter() {
        return Setups.SLS_TEST;
    }

}
