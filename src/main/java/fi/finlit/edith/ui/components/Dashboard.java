/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Dashboard provides
 *
 * @author tiwe
 * @version $Id$
 */
public class Dashboard extends AuthAwarePanel{
    
    @Inject
    private ComponentResources resources;
        
    public boolean isOnPage(String page){
        return resources.getPageName().toLowerCase().equals(page.toLowerCase());
    }

}
