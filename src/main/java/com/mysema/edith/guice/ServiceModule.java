package com.mysema.edith.guice;

import java.util.Map.Entry;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.mysema.edith.services.*;

public class ServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new JpaPersistModule("edith"));
        bindProperties();
        bind(JpaInitializer.class).asEagerSingleton();
        bind(DataInitService.class).asEagerSingleton();
//        bind(AuthService.class, AuthServiceImpl.class);
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
            for (Entry entry : getProperties().entrySet()) {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                if (value.equals("true") || value.equals("false")) {
                    bind(Boolean.class).annotatedWith(Names.named(key))
                        .toInstance(Boolean.parseBoolean(value));
                } else {
                    bind(String.class).annotatedWith(Names.named(key))
                        .toInstance(value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }        
    }
    
    protected <T> void bind(Class<T> iface, Class<? extends T> impl) {
        bind(iface).to(impl).in(Scopes.SINGLETON);
    }
    
    // XXX maybe return a Map<String,Object> instead?
    protected Properties getProperties() throws Exception {        
        Properties props = new Properties();        
        props.load(ServiceModule.class.getResourceAsStream("/edith.properties"));
        props.putAll(System.getProperties());
        return props;
    }
    
}
