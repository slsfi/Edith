package com.mysema.edith.domain;

public enum Profile {

    ADMIN("ROLE_USER", "ROLE_ADMIN"),

    USER("ROLE_USER");
    
    private final String[] roles;

    private Profile(String... roleNames) {
        this.roles = roleNames;
    }

    public String[] getRoles() {
        return roles;
    }
}
