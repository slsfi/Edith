/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.components.document;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.ui.services.DocumentRenderer;

/**
 * ViewPanel provides
 *
 * @author tiwe
 * @version $Id$
 */
public class View {

    @Inject
    private DocumentRenderer renderer;
    
    @Parameter
    private DocumentRevision document;

    @BeginRender
    void beginRender(MarkupWriter writer) throws Exception {
        renderer.renderDocument(document, writer);
    }
}
