/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;

import com.mysema.rdfbean.annotations.ClassMapping;

import fi.finlit.edith.EDITH;

@ClassMapping(ns=EDITH.NS)
public enum Profile {

    /**
     *
     */
    Admin("ROLE_USER","ROLE_ADMIN"),

    /**
     *
     */
    User("ROLE_USER");

    private final GrantedAuthority[] authorities;

    private Profile(String... roleNames){
        authorities = new GrantedAuthority[roleNames.length];
        for (int i = 0; i < authorities.length; i++){
            authorities[i] = new GrantedAuthorityImpl(roleNames[i]);
        }
    }

    public GrantedAuthority[] getAuthorities() {
        return authorities;
    }
}
