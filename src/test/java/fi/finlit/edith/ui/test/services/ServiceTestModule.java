/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.test.services;

import nu.localhost.tapestry5.springsecurity.services.internal.SaltSourceImpl;

import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.providers.encoding.ShaPasswordEncoder;

/**
 * ServiceTestModule provides
 *
 * @author tiwe
 * @version $Id$
 */
public class ServiceTestModule {

    public static SaltSource buildSaltSource() throws Exception{
        SaltSourceImpl saltSource = new SaltSourceImpl();
        saltSource.setSystemWideSalt("DEADBEEF");
        saltSource.afterPropertiesSet();
        return saltSource;
    }
    
    public static PasswordEncoder buildPaswordEncoder(){
        return new ShaPasswordEncoder();
    }
}
