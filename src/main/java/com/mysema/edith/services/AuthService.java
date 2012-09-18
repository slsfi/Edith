/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
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
