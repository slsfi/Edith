/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

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
