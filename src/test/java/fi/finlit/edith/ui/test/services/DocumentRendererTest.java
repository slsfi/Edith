package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.internal.services.MarkupWriterImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import fi.finlit.edith.ui.services.DocumentRenderer;

/**
 * TEITest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRendererTest extends AbstractServiceTest{

    @Inject
    private DocumentRenderer renderer;
    
    @Test
    public void test() throws Exception{
        File file = new File("etc/demo-material/tei/Nummisuutarit rakenteistettuna.xml");
        assertTrue(file + " doesn't exist", file.exists());
        MarkupWriter writer = new MarkupWriterImpl();
        renderer.render(file, writer);
        System.out.println(writer);
    }
    
}
