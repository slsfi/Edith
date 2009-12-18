/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.editio.ui.services;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

/**
 * UserDetailsImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("serial")
public class UserDetailsImpl implements UserDetails{
    
    private String username, password;
    
    private boolean nonExpired = true, nonLocked = true, enabled = true;

    private GrantedAuthority[] authorities;
    
    public UserDetailsImpl(String username, String password, GrantedAuthority... auth){
        this.username = username;
        this.password = password;
        this.authorities = auth;
    }
    
    @Override
    public GrantedAuthority[] getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password){
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return nonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return nonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return nonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
