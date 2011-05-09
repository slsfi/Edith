/**
 * 
 */
package fi.finlit.edith.ui.test.services;

import fi.finlit.edith.ui.services.AuthService;

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