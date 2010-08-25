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

    private static final String doc1 = "/documents/trunk/Nummisuutarit rakenteistettuna.xml";

    private static final String doc2 = "/documents/trunk/Nummisuutarit rakenteistettuna-annotoituna.xml";

    @Inject
    private DocumentRenderer renderer;

    @Inject
    private DocumentRepository docRepo;

    private final MarkupWriter writer = new MarkupWriterImpl();

    @Test
    public void renderDocument() throws Exception {
        renderer.renderDocument(docRepo.getDocumentForPath(doc1).getRevision(-1), writer);
    }

    @Test
    public void renderPageLinks() throws Exception {
        renderer.renderPageLinks(docRepo.getDocumentForPath(doc1).getRevision(-1), writer);
    }

    @Test
    public void renderDocumentWithNotes() throws Exception {
        renderer.renderDocument(docRepo.getDocumentForPath(doc2).getRevision(-1), writer);
    }
}
