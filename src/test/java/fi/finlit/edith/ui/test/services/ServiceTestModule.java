/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import java.io.File;
import java.io.IOException;

import nu.localhost.tapestry5.springsecurity.services.internal.SaltSourceImpl;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.providers.encoding.ShaPasswordEncoder;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import com.mysema.query.sql.H2Templates;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.rdfbean.Namespaces;
import com.mysema.rdfbean.model.IdSequence;
import com.mysema.rdfbean.model.MemoryIdSequence;
import com.mysema.rdfbean.model.Repository;
import com.mysema.rdfbean.object.Configuration;
import com.mysema.rdfbean.object.DefaultConfiguration;
import com.mysema.rdfbean.rdb.RDBRepository;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.ui.services.AuthService;

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

    public static final String TEST_DOCUMENT_CONTENT_KEY = "test.document.content";

    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration)
            throws IOException, SVNException {
        File svnRepo = new File("target/repo");
        configuration.add(NOTE_TEST_DATA_KEY, "etc/demo-material/notes/nootit.xml");
        File testDocumentFile = new File("etc/demo-material/tei/Nummisuutarit rakenteistettuna.xml");
        configuration.add(TEST_DOCUMENT_FILE_KEY, testDocumentFile.getPath());
        configuration.add(TEST_DOCUMENT_CONTENT_KEY, FileUtils.readFileToString(testDocumentFile,"UTF-8"));
        configuration.add(TEST_DOCUMENT_KEY, "/documents/trunk/Nummisuutarit rakenteistettuna.xml");
        configuration.add(EDITH.REPO_FILE_PROPERTY, svnRepo.getAbsolutePath());
        configuration.add(EDITH.REPO_URL_PROPERTY, SVNURL.fromFile(svnRepo).toString());
    }

    public static SaltSource buildSaltSource() throws Exception {
        SaltSourceImpl saltSource = new SaltSourceImpl();
        saltSource.setSystemWideSalt("DEADBEEF");
        saltSource.afterPropertiesSet();
        return saltSource;
    }

    /**
     * Makes a memory based test configuration override
     */
    public static void contributeServiceOverride(
            MappedConfiguration<Class<?>, Object> configuration) {
        Namespaces.register("edith", EDITH.NS);
        JdbcConnectionPool dataSource = JdbcConnectionPool.create("jdbc:h2:mem:test", "sa", "");   
        dataSource.setMaxConnections(30);
        IdSequence idSequence = new MemoryIdSequence();
        SQLTemplates templates = new H2Templates();
        Configuration conf = new DefaultConfiguration(Document.class.getPackage());
        RDBRepository repository = new RDBRepository(conf, dataSource, templates, idSequence);
//        MemoryRepository repository = new MemoryRepository();
//        repository.setSources(new RDFSource("classpath:/edith.ttl",Format.TURTLE, EDITH.NS));
        configuration.add(Repository.class, repository);

        AuthService authService = new AuthService() {
            @Override
            public String getUsername() {
                return "timo";
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void logout() {
            }
        };
        configuration.add(AuthService.class, authService);

    }

    public static PasswordEncoder buildPaswordEncoder() {
        return new ShaPasswordEncoder();
    }

}
