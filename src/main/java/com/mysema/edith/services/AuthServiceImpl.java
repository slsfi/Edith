/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class AuthServiceImpl implements AuthService {

    @Override
    public boolean isAuthenticated() {
        Subject currentUser = SecurityUtils.getSubject();
        return currentUser != null && currentUser.isAuthenticated();
    }

    @Override
    public void logout() {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser != null) {
            currentUser.logout();
        }
    }

    @Override
    public String getUsername() {
        Subject currentUser = SecurityUtils.getSubject();
        return currentUser != null ? currentUser.toString() : null;
    }

}
