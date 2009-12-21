/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.components;

import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.ui.services.AuthService;

/**
 * AuthAwarePanel provides
 *
 * @author tiwe
 * @version $Id$
 */
public abstract class AuthAwarePanel {
    
    @Inject
    private AuthService authService;
    
    public boolean isLoggedIn(){
        return authService.isAuthenticated();        
    }

    public String getUsername(){
        return authService.getUsername();
    }

}
