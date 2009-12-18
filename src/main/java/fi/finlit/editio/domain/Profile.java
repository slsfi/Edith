/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.editio.domain;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;

import com.mysema.rdfbean.annotations.ClassMapping;

import fi.finlit.editio.EDITIO;

/**
 * Profile provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITIO.NS)
public enum Profile {

    /**
     * 
     */
    User("ROLE_USER"),
    
    /**
     * 
     */
    Admin("ROLE_USER","ROLE_ADMIN");
    
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
