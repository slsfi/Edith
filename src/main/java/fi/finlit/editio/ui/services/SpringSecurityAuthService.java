/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.editio.ui.services;

import nu.localhost.tapestry5.springsecurity.services.LogoutService;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;

/**
 * ShiroAuthService provides
 *
 * @author tiwe
 * @version $Id$
 */
public class SpringSecurityAuthService implements AuthService{
    
    @Inject
    private LogoutService logoutService;

    @Override
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getPrincipal() instanceof UserDetails;
    }

    @Override
    public void logout() {
        logoutService.logout();
    }

    @Override
    public String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

}
