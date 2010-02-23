/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.mixins;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.components.PageLink;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * ExternalLinkMixin provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
public class ExternalMixin {

    @InjectContainer
    private PageLink pageLink;

    @Parameter(defaultPrefix="inherit:")
    private String pageName;

    @Inject
    private ComponentResources resources;

    public void beforeRender(MarkupWriter writer) {
        boolean onPage = resources.getPageName().equalsIgnoreCase(pageName);
        if (onPage){
            writer.writeRaw("disabled");
        }
    }

}
