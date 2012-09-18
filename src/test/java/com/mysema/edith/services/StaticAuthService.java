/**
 * 
 */
package com.mysema.edith.services;

import com.mysema.edith.services.AuthService;


public final class StaticAuthService implements AuthService {
    
    @Override
    public String getUsername() {
        return "timo";
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void logout() {
    }
}