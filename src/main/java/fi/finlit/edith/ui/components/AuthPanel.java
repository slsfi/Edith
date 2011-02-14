/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ioc.annotations.Inject;

public class AuthPanel extends AuthAwarePanel{

    @Inject
    private ComponentResources resources;

    public boolean isOnPage(String page){
        return resources.getPageName().equalsIgnoreCase(page);
    }

}
