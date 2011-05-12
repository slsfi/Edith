package fi.finlit.edith.ui.test.pages;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.internal.services.ArrayEventContext;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fi.finlit.edith.EdithTestConstants;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.ui.pages.document.Publish;
import fi.finlit.edith.ui.services.DocumentRepository;
import fi.finlit.edith.ui.test.services.AbstractServiceTest;

@Ignore
public class PublishTest extends AbstractServiceTest{
    
    @Autobuild
    @Inject
    private Publish publishPage;
    
    @Inject
    @Symbol(EdithTestConstants.TEST_DOCUMENT_KEY)
    private String testDocument;
    
    @Inject
    private DocumentRepository repository;
    
    @Inject
    private TypeCoercer typeCoercer;
    
    @Before
    public void setUp() throws IOException{
        Document document = repository.getOrCreateDocumentForPath(testDocument);
        EventContext context = new ArrayEventContext(typeCoercer, new Object[]{document.getId()});
        publishPage.onActivate(context);
    }
    
    @Test
    public void onActionFromPublish() throws IOException, XMLStreamException{
        publishPage.onActionFromPublish("X");
    }

}
