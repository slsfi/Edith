/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static fi.finlit.edith.EdithTestConstants.NOTE_TEST_DATA_KEY;
import static fi.finlit.edith.EdithTestConstants.TEST_DOCUMENT_CONTENT_KEY;
import static fi.finlit.edith.EdithTestConstants.TEST_DOCUMENT_FILE_KEY;
import static fi.finlit.edith.EdithTestConstants.TEST_DOCUMENT_KEY;

import java.io.File;
import java.io.IOException;

import nu.localhost.tapestry5.springsecurity.services.internal.SaltSourceImpl;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.providers.encoding.ShaPasswordEncoder;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.ui.services.AuthService;

public class ServiceTestModule {

    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration) throws IOException, SVNException {
        File svnRepo = new File("target/repo");
        configuration.add(NOTE_TEST_DATA_KEY, "etc/demo-material/notes/nootit.xml");
        File testDocumentFile = new File("etc/demo-material/tei/Nummisuutarit rakenteistettuna.xml");
        configuration.add(TEST_DOCUMENT_FILE_KEY, testDocumentFile.getPath());
        configuration.add(TEST_DOCUMENT_CONTENT_KEY, FileUtils.readFileToString(testDocumentFile, "UTF-8"));
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

    public static void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration) {
        AuthService authService = new StaticAuthService();
        configuration.add(AuthService.class, authService);
    }
    
    public static PasswordEncoder buildPaswordEncoder() {
        return new ShaPasswordEncoder();
    }

}
