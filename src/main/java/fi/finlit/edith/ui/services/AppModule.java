/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import nu.localhost.tapestry5.springsecurity.services.RequestInvocationDefinition;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.ValueEncoderFactory;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.providers.encoding.ShaPasswordEncoder;
import org.springframework.security.userdetails.UserDetailsService;

import fi.finlit.edith.ui.services.hibernate.UserDetailsServiceImpl;

/**
 * AppModule is the main Tapestry module for the Edith application
 *
 * @author tiwe
 * @version $Id$
 */
@SubModule( { HibernateDataModule.class, ServiceModule.class, HibernateServiceModule.class })
public final class AppModule {

    private AppModule() {}

    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration) {
        // general config
        configuration.add(SymbolConstants.SUPPORTED_LOCALES, "fi,en,sv,de");
        configuration.add(SymbolConstants.PRODUCTION_MODE, System.getProperty("production.mode", "false"));

        //Disable automatic form autofocus
        configuration.add(SymbolConstants.FORM_CLIENT_LOGIC_ENABLED, "false");

        // Spring Security config
        configuration.add("spring-security.loginform.url", "/login");
//        configuration.add("spring-security.force.ssl.login", "true");
        configuration.add("spring-security.check.url", "/security_check");
        configuration.add("spring-security.failure.url", "/loginfailed");
    }

    public static void contributeFilterSecurityInterceptor(
            Configuration<RequestInvocationDefinition> configuration) {

        // Login and about page are the only ones allowed for anonymous users
        configuration.add(new RequestInvocationDefinition("/loginfailed", "ROLE_ANONYMOUS"));
        configuration.add(new RequestInvocationDefinition("/security_check", "ROLE_ANONYMOUS"));
        configuration.add(new RequestInvocationDefinition("/login", "ROLE_ANONYMOUS"));
        configuration.add(new RequestInvocationDefinition("/favicon.ico", "ROLE_ANONYMOUS,ROLE_USER"));
        configuration.add(new RequestInvocationDefinition("/about", "ROLE_ANONYMOUS,ROLE_USER"));
        configuration.add(new RequestInvocationDefinition("/assets/**", "ROLE_ANONYMOUS,ROLE_USER"));
        configuration.add(new RequestInvocationDefinition("/**", "ROLE_USER"));
    }

    public static void bind(ServiceBinder binder){
        binder.bind(UserDetailsService.class, UserDetailsServiceImpl.class);
    }

    public static void contributeServiceOverride(MappedConfiguration<Class<?>,Object> configuration) {
      configuration.add(PasswordEncoder.class, new ShaPasswordEncoder());
    }

    public static void contributeProviderManager(
            OrderedConfiguration<AuthenticationProvider> configuration,
            @InjectService("DaoAuthenticationProvider") AuthenticationProvider daoAuthenticationProvider) {
        configuration.add("daoAuthenticationProvider", daoAuthenticationProvider);
    }

    public static void contributeClasspathAssetAliasManager(
            MappedConfiguration<String, String> configuration) {
        //Javascript
        configuration.add("js", "js");
    }

    @SuppressWarnings("unchecked")
    public static void contributeValueEncoderSource(
            MappedConfiguration<Class<?>, ValueEncoderFactory<?>> configuration/*,
            final com.mysema.rdfbean.object.Configuration rdfBeanConfiguration,
            final SessionFactory sessionFactory*/){
        /*
        for (Class<?> cl : Arrays.<Class<?>>asList(
                DocumentNote.class,
                Document.class,
                Note.class)){
            configuration.add(cl, new EntityValueEncoderFactory(sessionFactory, rdfBeanConfiguration, cl));
        }

        configuration.add(UserInfo.class, new UserInfoValueEncoderFactory());*/
        configuration.add(fi.finlit.edith.dto.UserInfo.class, new UserInfoValueEncoderFactory());
    }


}
