/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages;

import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.ui.services.AuthService;

public abstract class Base {

    @Inject
    private AuthService authService;

    public boolean isLoggedIn(){
        return authService.isAuthenticated();
    }

    public String getUsername(){
        return authService.getUsername();
    }

}
