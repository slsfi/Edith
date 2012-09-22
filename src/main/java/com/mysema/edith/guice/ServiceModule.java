/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.guice;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.mysema.edith.services.*;
import com.mysema.edith.web.Converter;

public class ServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        // TODO provide properties also from somewhere else 
        install(new JpaPersistModule("edith").properties(System.getProperties())); 
        bindProperties();
        bind(Converter.class).in(Scopes.SINGLETON);        
        bind(JpaInitializer.class).asEagerSingleton();
        bind(DataInitService.class).asEagerSingleton();
        bind(ContentRenderer.class, ContentRendererImpl.class);
        bind(DocumentDao.class, DocumentDaoImpl.class);
        bind(DocumentNoteDao.class, DocumentNoteDaoImpl.class);
        bind(NoteDao.class, NoteDaoImpl.class);
        bind(PersonDao.class, PersonDaoImpl.class);
        bind(PlaceDao.class, PlaceDaoImpl.class);
        bind(SubversionService.class, SubversionServiceImpl.class);
        bind(TermDao.class, TermDaoImpl.class);
        bind(UserDao.class, UserDaoImpl.class);
    }
    
    private void bindProperties() {
        try {
            for (Entry<String, Object> entry : getProperties().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                bind((Class)value.getClass()).annotatedWith(Names.named(key))
                    .toInstance(value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }        
    }
    
    protected <T> void bind(Class<T> iface, Class<? extends T> impl) {
        bind(iface).to(impl).in(Scopes.SINGLETON);
    }
    
    protected Map<String, Object> getProperties() throws Exception {        
        Properties props = new Properties();        
        props.load(ServiceModule.class.getResourceAsStream("/edith.properties"));
        props.putAll(System.getProperties());
        Map<String, Object> rv = new HashMap<String, Object>();
        Set<String> booleans = Sets.newHashSet("true", "false");
        for (Entry entry : props.entrySet()) {
            String key = entry.getKey().toString();
            if (booleans.contains(entry.getValue())){
                rv.put(key, Boolean.valueOf(entry.getValue().toString()));
            } else {
                rv.put(key, entry.getValue());
            }
        }
        return rv;
    }
    
}
