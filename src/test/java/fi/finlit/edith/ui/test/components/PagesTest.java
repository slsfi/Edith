package fi.finlit.edith.ui.test.components;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.internal.services.MarkupWriterImpl;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.Test;

import fi.finlit.edith.EdithTestConstants;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.ui.components.document.Pages;
import fi.finlit.edith.ui.services.DocumentRepository;
import fi.finlit.edith.ui.test.services.AbstractServiceTest;

public class PagesTest extends AbstractServiceTest{

    @Inject
    @Symbol(EdithTestConstants.TEST_DOCUMENT_KEY)
    private String testDocument;

    @Inject
    private DocumentRepository documentRepository;

    @Autobuild
    @Inject
    private Pages pages;

    @Test
    public void BeginRender() throws XMLStreamException, IOException{
        Document document = documentRepository.getDocumentForPath(testDocument);
        pages.setDocument(document.getRevision(-1));

        MarkupWriter writer = new MarkupWriterImpl();
        pages.beginRender(writer);
        assertTrue(writer.toString().startsWith("<ul "));
        assertTrue(writer.toString().endsWith("</ul>"));
    }

}
