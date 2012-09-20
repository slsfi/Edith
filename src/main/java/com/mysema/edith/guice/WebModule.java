/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.guice;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import com.mysema.edith.web.DocumentNoteService;
import com.mysema.edith.web.DocumentService;
import com.mysema.edith.web.NoteService;
import com.mysema.edith.web.PersonService;
import com.mysema.edith.web.PlaceService;
import com.mysema.edith.web.UserService;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class WebModule extends ServletModule {
    
    @Override
    protected void configureServlets() {
        install(new ServiceModule());
        // bind resource classes here
        bind(DocumentNoteService.class).in(Scopes.SINGLETON);
        bind(DocumentService.class).in(Scopes.SINGLETON);
        bind(NoteService.class).in(Scopes.SINGLETON);
        bind(PersonService.class).in(Scopes.SINGLETON);
        bind(PlaceService.class).in(Scopes.SINGLETON);
        bind(UserService.class).in(Scopes.SINGLETON);
        
        bind(GuiceContainer.class);
        bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
        serve("/api/*").with(GuiceContainer.class);
    }
    
}
