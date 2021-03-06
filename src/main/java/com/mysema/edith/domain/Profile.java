/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.domain;

public enum Profile {

    Admin("ROLE_USER", "ROLE_ADMIN"),

    User("ROLE_USER");
    
    private final String[] roles;

    private Profile(String... roleNames) {
        this.roles = roleNames;
    }

    public String[] getRoles() {
        return roles;
    }
}
