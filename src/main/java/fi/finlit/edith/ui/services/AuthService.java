/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

public interface AuthService {

    boolean isAuthenticated();

    void logout();

    String getUsername();

}
