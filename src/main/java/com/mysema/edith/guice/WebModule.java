package com.mysema.edith.guice;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class WebModule extends ServletModule {
    
    @Override
    protected void configureServlets() {
        install(new ServiceModule());
        // bind resource classes here
        bind(GuiceContainer.class);
        bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
        serve("/*").with(GuiceContainer.class);
    }
}
