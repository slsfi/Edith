/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

/**
 * AuthFacade provides
 *
 * @author tiwe
 * @version $Id$
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
