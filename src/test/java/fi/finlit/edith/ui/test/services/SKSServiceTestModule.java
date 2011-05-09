package fi.finlit.edith.ui.test.services;

import java.io.IOException;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.tmatesoft.svn.core.SVNException;

import fi.finlit.edith.EDITH;

public class SKSServiceTestModule {
    
    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration) throws IOException, SVNException {
        ServiceTestModule.contributeApplicationDefaults(configuration);
        configuration.add(EDITH.EXTENDED_TERM, "false");
    }
    
    public static SaltSource buildSaltSource() throws Exception {
        return ServiceTestModule.buildSaltSource();
    }

    public static void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration) {
        ServiceTestModule.contributeServiceOverride(configuration);
    }

    public static PasswordEncoder buildPaswordEncoder() {
        return ServiceTestModule.buildPaswordEncoder();
    }

}
