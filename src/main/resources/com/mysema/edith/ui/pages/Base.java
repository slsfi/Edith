/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.pages;

import com.mysema.edith.services.AuthService;

public abstract class Base {

    @Inject
    private AuthService authService;

    public boolean isLoggedIn() {
        return authService.isAuthenticated();
    }

    public String getUsername() {
        return authService.getUsername();
    }

}
