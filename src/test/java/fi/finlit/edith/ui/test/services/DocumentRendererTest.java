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

import fi.finlit.edith.domain.DocumentRevision;
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
        MarkupWriter writer = new MarkupWriterImpl();
        String svnPath = "/documents/trunk/Nummisuutarit rakenteistettuna.xml";
        renderer.renderDocument(new DocumentRevision(svnPath, -1), writer);
        System.out.println(writer);
    }
    
}