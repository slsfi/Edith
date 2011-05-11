package fi.finlit.edith.ui.web;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mysema.commons.jetty.JettyConfig;
import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.SLSEdithDebugStart;

public class SLSAnnotateTest extends Selenium {

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
        
    }

    @Override
    public String locales() {
        return "sv,fi,en";
    }

    @Override
    public WebappStarter starter() {
        return new SLSEdithDebugStart() {
            @Override
            public JettyConfig configure() throws Exception {
                root = "target/sls-test/";
                return super.configure().setPort(8090);
            }

        };
    }


}
