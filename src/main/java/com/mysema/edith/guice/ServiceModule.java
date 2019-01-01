/*
 * Copyright (c) 2018 Mysema
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
import com.mysema.edith.services.ContentRenderer;
import com.mysema.edith.services.ContentRendererImpl;
import com.mysema.edith.services.DocumentDao;
import com.mysema.edith.services.DocumentDaoImpl;
import com.mysema.edith.services.DocumentNoteDao;
import com.mysema.edith.services.DocumentNoteDaoImpl;
import com.mysema.edith.services.DocumentNoteService;
import com.mysema.edith.services.DocumentNoteServiceImpl;
import com.mysema.edith.services.DocumentXMLDao;
import com.mysema.edith.services.DocumentXMLDaoImpl;
import com.mysema.edith.services.NoteCommentDao;
import com.mysema.edith.services.NoteCommentDaoImpl;
import com.mysema.edith.services.NoteDao;
import com.mysema.edith.services.NoteDaoImpl;
import com.mysema.edith.services.PersonDao;
import com.mysema.edith.services.PersonDaoImpl;
import com.mysema.edith.services.PlaceDao;
import com.mysema.edith.services.PlaceDaoImpl;
import com.mysema.edith.services.TermDao;
import com.mysema.edith.services.TermDaoImpl;
import com.mysema.edith.services.UserDao;
import com.mysema.edith.services.UserDaoImpl;
import com.mysema.edith.services.VersioningDao;
import com.mysema.edith.services.VersioningDaoImpl;
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
        bind(DocumentXMLDao.class, DocumentXMLDaoImpl.class);
        bind(DocumentNoteDao.class, DocumentNoteDaoImpl.class);
        bind(DocumentNoteService.class, DocumentNoteServiceImpl.class);
        bind(NoteDao.class, NoteDaoImpl.class);
        bind(NoteCommentDao.class, NoteCommentDaoImpl.class);
        bind(PersonDao.class, PersonDaoImpl.class);
        bind(PlaceDao.class, PlaceDaoImpl.class);
        bind(VersioningDao.class, VersioningDaoImpl.class);
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
