/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.test.services;

import nu.localhost.tapestry5.springsecurity.services.internal.SaltSourceImpl;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.providers.encoding.ShaPasswordEncoder;

import com.mysema.rdfbean.model.Repository;
import com.mysema.rdfbean.model.io.Format;
import com.mysema.rdfbean.model.io.RDFSource;
import com.mysema.rdfbean.sesame.MemoryRepository;

import fi.finlit.edith.EDITH;

/**
 * ServiceTestModule provides
 * 
 * @author tiwe
 * @version $Id$
 */
public class ServiceTestModule {

    public static SaltSource buildSaltSource() throws Exception {
        SaltSourceImpl saltSource = new SaltSourceImpl();
        saltSource.setSystemWideSalt("DEADBEEF");
        saltSource.afterPropertiesSet();
        return saltSource;
    }

    /**
     * Makes a memory based test configuration override
     */
    public static void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration) {
        MemoryRepository repository = new MemoryRepository();
        repository.setSources(new RDFSource("classpath:/edith.ttl", Format.TURTLE, EDITH.NS));
        configuration.add(Repository.class, repository);
    }

    public static PasswordEncoder buildPaswordEncoder() {
        return new ShaPasswordEncoder();
    }
}
