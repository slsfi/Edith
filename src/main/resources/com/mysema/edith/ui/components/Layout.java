/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.components;

import org.hibernate.annotations.Parameter;

import com.sun.xml.internal.ws.api.PropertySet.Property;

@Import(stylesheet = { "context:styles/base.css", "context:styles/layout-3col.css",
        "context:styles/edith.css",

        // tapestry component styles
        "context:styles/tapestry/forms.css", "context:styles/tapestry/grid.css" })
@SuppressWarnings("unused")
public class Layout {
    @Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
    @Property
    private Block title;

    @Property
    private String pageName;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    @Property
    private Block leftPanel;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    @Property
    private Block rightPanel;

    @Inject
    private ComponentResources resources;

    public boolean isOnPage(String page) {
        return resources.getPageName().equalsIgnoreCase(page);
    }

}
