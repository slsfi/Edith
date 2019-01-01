/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.services;

/**
 * @author tiwe
 *
 */
public interface AuthService {

    /**
     * @return
     */
    boolean isAuthenticated();

    /**
     * 
     */
    void logout();

    /**
     * @return
     */
    String getUsername();

}
