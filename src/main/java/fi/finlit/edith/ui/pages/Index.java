/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages;

import org.springframework.security.annotation.Secured;

/**
 * Index provides
 *
 * @author tiwe
 * @version $Id$
 */
public class Index {
    
    @Secured("ROLE_USER")
    void onActivate(){
        
    }

}
