/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.mixins;

import org.hibernate.annotations.Parameter;

@SuppressWarnings("unused")
public class ExternalMixin {

    @InjectContainer
    private PageLink pageLink;

    @Parameter(defaultPrefix = "inherit:")
    private String pageName;

    @Inject
    private ComponentResources resources;

    public void beforeRender(MarkupWriter writer) {
        boolean onPage = resources.getPageName().equalsIgnoreCase(pageName);
        if (onPage) {
            writer.writeRaw("disabled");
        }
    }

}
