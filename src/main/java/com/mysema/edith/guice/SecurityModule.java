/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.mysema.edith.services.AuthService;
import com.mysema.edith.services.AuthServiceImpl;

public class SecurityModule extends AbstractModule {
    
    @Override
    protected void configure() {
        bind(AuthService.class).to(AuthServiceImpl.class).in(Scopes.SINGLETON);   
    }

}
