/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import java.io.File;
import java.io.IOException;

import nu.localhost.tapestry5.springsecurity.services.internal.SaltSourceImpl;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.providers.encoding.ShaPasswordEncoder;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import com.mysema.rdfbean.Namespaces;
import com.mysema.rdfbean.model.Repository;
import com.mysema.rdfbean.model.io.Format;
import com.mysema.rdfbean.model.io.RDFSource;
import com.mysema.rdfbean.sesame.MemoryRepository;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.ui.services.AuthService;
import fi.finlit.edith.ui.services.DocumentRepositoryImpl;
import fi.finlit.edith.ui.services.SubversionServiceImpl;

/**
 * ServiceTestModule provides
 *
 * @author tiwe
 * @version $Id$
 */
public class ServiceTestModule {

    public static final String NOTE_TEST_DATA_KEY = "note.test.data";

    public static final String TEST_DOCUMENT_KEY = "test.document";
    
    public static final String TEST_DOCUMENT_FILE_KEY = "test.document.file";

    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration)
            throws IOException, SVNException {
        File svnRepo = new File("target/repo");
        configuration.add(NOTE_TEST_DATA_KEY, "etc/demo-material/notes/nootit.xml");
        configuration.add(TEST_DOCUMENT_FILE_KEY, "etc/demo-material/tei/Nummisuutarit rakenteistettuna-annotoituna.xml");
        configuration.add(TEST_DOCUMENT_KEY, "/documents/trunk/Nummisuutarit rakenteistettuna-annotoituna.xml");
        configuration.add(EDITH.REPO_FILE_PROPERTY, svnRepo.getAbsolutePath());
        configuration.add(EDITH.REPO_URL_PROPERTY, SVNURL.fromFile(svnRepo).toString());
    }
    
    public static SaltSource buildSaltSource() throws Exception {
        SaltSourceImpl saltSource = new SaltSourceImpl();
        saltSource.setSystemWideSalt("DEADBEEF");
        saltSource.afterPropertiesSet();
        return saltSource;
    }
    
    public static void bind(ServiceBinder binder){
        binder.bind(DocumentRepositoryImpl.class).withId("DocumentRepositoryImpl");
        binder.bind(SubversionServiceImpl.class).withId("SubversionServiceImpl");
    }

    /**
     * Makes a memory based test configuration override
     */
    public static void contributeServiceOverride(
            MappedConfiguration<Class<?>, Object> configuration) {
        Namespaces.register("edith", EDITH.NS);
        MemoryRepository repository = new MemoryRepository();
        repository.setSources(new RDFSource("classpath:/edith.ttl",Format.TURTLE, EDITH.NS));
        configuration.add(Repository.class, repository);
        
        AuthService authService = new AuthService() {
            public String getUsername() {
                return "timo";
            }

            public boolean isAuthenticated() {
                return true;
            }

            public void logout() {
            }
        };
        configuration.add(AuthService.class, authService);
        
//        configuration.add(IdentityService.class, MemoryIdentityService.instance());
    }

    public static PasswordEncoder buildPaswordEncoder() {
        return new ShaPasswordEncoder();
    }

}
