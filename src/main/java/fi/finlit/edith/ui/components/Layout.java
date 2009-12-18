/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Layout provides
 *
 * @author tiwe
 * @version $Id$
 */
@IncludeStylesheet({
    "context:styles/base.css",
    "context:styles/general.css",
    "context:styles/edith.css",    
    
    "context:styles/tapestry/forms.css",
    "context:styles/tapestry/grid.css",
    
    "context:styles/skins/simple.css"
    })
@SuppressWarnings("unused")
public class Layout {
    
    
    @Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
    @Property
    private Block title;

    @Property
    private String pageName;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    @Property
    private Block eastPanel;
    
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    @Property
    private Block westPanel;
        
    @Inject
    private ComponentResources resources;
        
    public boolean isOnPage(String page){
        return resources.getPageName().toLowerCase().equals(page.toLowerCase());
    }

}
