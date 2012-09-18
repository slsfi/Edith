/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.components;

import com.google.inject.Inject;

public class AuthPanel extends AuthAwarePanel {

    @Inject
    private ComponentResources resources;

    public boolean isOnPage(String page) {
        return resources.getPageName().equalsIgnoreCase(page);
    }

}
