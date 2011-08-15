package fi.finlit.edith.ui.test.sls;

import java.io.IOException;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.tmatesoft.svn.core.SVNException;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.ui.services.ServiceTestModule;

public class SLSServiceTestModule {
    
    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration) throws IOException, SVNException {
        ServiceTestModule.contributeApplicationDefaults(configuration);
        configuration.add(EDITH.EXTENDED_TERM, "true");
    }
    
    public static SaltSource buildSaltSource() throws Exception {
        return ServiceTestModule.buildSaltSource();
    }

    public static PasswordEncoder buildPaswordEncoder() {
        return ServiceTestModule.buildPaswordEncoder();
    }

}
