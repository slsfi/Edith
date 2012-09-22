package com.mysema.edith.guice;

import java.util.Map;

import com.mysema.edith.EDITH;


public class SKSServiceTestModule extends ServiceTestModule {
    
    @Override
    protected Map<String, Object> getProperties() throws Exception  {
        Map<String, Object> properties = super.getProperties();
        properties.put(EDITH.EXTENDED_TERM, false);
        return properties;
    }    
    
//    public static void contributeApplicationDefaults(
//            MappedConfiguration<String, String> configuration) throws IOException, SVNException {
//        ServiceTestModule.contributeApplicationDefaults(configuration);
//        configuration.add(EDITH.EXTENDED_TERM, "false");
//    }
//    
//    public static SaltSource buildSaltSource() throws Exception {
//        return ServiceTestModule.buildSaltSource();
//    }
//    
//    public static void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration) {
//        ServiceTestModule.contributeServiceOverride(configuration);
//    }
//
//    public static PasswordEncoder buildPaswordEncoder() {
//        return ServiceTestModule.buildPaswordEncoder();
//    }
//
//    public static Response buildResponse() {
//        return ServiceTestModule.buildResponse();
//    }

    
}
