/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.internal.services.MarkupWriterImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.ui.services.DocumentRenderer;

/**
 * TEITest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRendererTest extends AbstractServiceTest {

    @Inject
    private DocumentRenderer renderer;
    
    @Inject
    private DocumentRepository docRepo;

    @Test
    public void renderDocument() throws Exception {
        MarkupWriter writer = new MarkupWriterImpl();
        String svnPath = "/documents/trunk/Nummisuutarit rakenteistettuna.xml";
        renderer.renderDocument(docRepo.getDocumentForPath(svnPath).getRevision(-1), writer);
        System.out.println(writer.toString());
    }

    @Test
    public void renderPageLinks() throws Exception {
        MarkupWriter writer = new MarkupWriterImpl();
        String svnPath = "/documents/trunk/Nummisuutarit rakenteistettuna.xml";
        renderer.renderPageLinks(docRepo.getDocumentForPath(svnPath).getRevision(-1), writer);
    }

    @Test
    public void renderDocumentWithNotes() throws Exception {
        MarkupWriter writer = new MarkupWriterImpl();
        String svnPath = "/documents/trunk/Nummisuutarit rakenteistettuna-annotoituna.xml";
        renderer.renderDocument(docRepo.getDocumentForPath(svnPath).getRevision(-1), writer);
    }

    @Override
    protected Class<?> getServiceClass() {
        return DocumentRenderer.class;
    }

}
