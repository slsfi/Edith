/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.pages;

import com.mysema.edith.services.AuthService;

public class Logout extends Base {

    @Inject
    private AuthService authService;

    public void onActivate() {
        authService.logout();
    }

}