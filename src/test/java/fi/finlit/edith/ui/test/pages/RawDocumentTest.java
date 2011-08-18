package fi.finlit.edith.ui.test.pages;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.Test;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.sql.domain.Document;
import fi.finlit.edith.ui.pages.document.RawDocument;
import fi.finlit.edith.ui.services.DocumentDao;
import fi.finlit.edith.ui.services.hibernate.AbstractHibernateTest;

public class RawDocumentTest extends AbstractHibernateTest {

    @Autobuild
    @Inject
    private RawDocument rawDocument;

    @Inject
    private DocumentDao documentDao;
    
    @Inject
    @Symbol(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;
    
    @Test
    public void OnActivate() throws IOException {
        Document document = getDocument("/Nummisuutarit rakenteistettuna.xml");
        StreamResponse streamResponse = rawDocument.onActivate(document.getId().toString());
        assertNotNull(streamResponse);
    }
    
    private Document getDocument(String path) {
        return documentDao.getDocumentForPath(documentRoot + path);
    }

    
}
