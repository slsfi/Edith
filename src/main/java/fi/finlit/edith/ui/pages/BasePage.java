/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages;

import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.ui.services.AuthService;

/**
 * BasePage provides
 *
 * @author tiwe
 * @version $Id$
 */
public abstract class BasePage {
    
    @Inject
    private AuthService authService;
    
    public boolean isLoggedIn(){
        return authService.isAuthenticated();
    }
    
    public String getUsername(){
        return authService.getUsername();
    }
        
}
