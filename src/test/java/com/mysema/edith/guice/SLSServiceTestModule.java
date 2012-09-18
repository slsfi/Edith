package com.mysema.edith.guice;

import java.util.Properties;

import com.mysema.edith.EDITH;


public class SLSServiceTestModule extends ServiceTestModule {
    
    @Override
    protected Properties getProperties() throws Exception  {
        Properties properties = super.getProperties();
        properties.put(EDITH.EXTENDED_TERM, "true");
        return properties;
    }
    
//    public static void contributeApplicationDefaults(
//            MappedConfiguration<String, String> configuration) throws IOException, SVNException {
//        ServiceTestModule.contributeApplicationDefaults(configuration);
//        configuration.add(EDITH.EXTENDED_TERM, "true");
//    }
//    
//    public static SaltSource buildSaltSource() throws Exception {
//        return ServiceTestModule.buildSaltSource();
//    }
//
//    public static PasswordEncoder buildPaswordEncoder() {
//        return ServiceTestModule.buildPaswordEncoder();
//    }

}
