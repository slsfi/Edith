/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

import fi.finlit.edith.EDITH;

@SuppressWarnings("unused")
public class Dashboard extends AuthAwarePanel{

    @Inject
    private ComponentResources resources;
    
    @Inject
    @Symbol(EDITH.EXTENDED_TERM)
    @Property
    private boolean slsMode;

    public boolean isOnPage(String page){
        return resources.getPageName().equalsIgnoreCase(page);
    }

}
