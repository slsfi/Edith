/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.dto;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails{

    private static final long serialVersionUID = -3810708516049551503L;

    private final String username;

    private String password;

    private static final boolean NON_EXPIRED = true, NON_LOCKED = true, ENABLED = true;

    private final GrantedAuthority[] authorities;

    public UserDetailsImpl(String username, String password, GrantedAuthority... auth){
        this.username = username;
        this.password = password;
        authorities = auth;
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
        return NON_EXPIRED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return NON_LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return NON_EXPIRED;
    }

    @Override
    public boolean isEnabled() {
        return ENABLED;
    }

}
