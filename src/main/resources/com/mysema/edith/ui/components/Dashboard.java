/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.components;

import com.mysema.edith.EDITH;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@SuppressWarnings("unused")
public class Dashboard extends AuthAwarePanel {

    @Inject
    private ComponentResources resources;

    @Inject
    @Symbol(EDITH.EXTENDED_TERM)
    @Property
    private boolean slsMode;

    public boolean isOnPage(String page) {
        return resources.getPageName().equalsIgnoreCase(page);
    }

}
