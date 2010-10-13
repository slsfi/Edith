/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.IOException;
import java.util.Arrays;

import nu.localhost.tapestry5.springsecurity.services.RequestInvocationDefinition;

import org.apache.commons.collections15.BeanMap;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.AliasContribution;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.ValueEncoderFactory;
import org.apache.tapestry5.urlrewriter.URLRewriterRule;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.providers.encoding.ShaPasswordEncoder;
import org.springframework.security.userdetails.UserDetailsService;

import com.mysema.rdfbean.model.RepositoryException;
import com.mysema.rdfbean.object.MappedProperty;
import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionFactory;
import com.mysema.tapestry.PageMappingRule;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.UserInfo;

/**
 * AppModule is the main Tapestry module for the Edith application
 *
 * @author tiwe
 * @version $Id$
 */
@SubModule( { DataModule.class, ServiceModule.class })
public final class AppModule {
    private AppModule() {}

    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration) {
        // general config
        configuration.add(SymbolConstants.SUPPORTED_LOCALES, "fi,en,sv,de");
        configuration.add(SymbolConstants.PRODUCTION_MODE, System.getProperty("production.mode", "false"));

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

    public static void contributeURLRewriter(
            OrderedConfiguration<URLRewriterRule> configuration,
            ComponentClassResolver componentResolver) {
        // strip "page" suffix off from page names
        configuration.add("pageMapping", new PageMappingRule(componentResolver));
    }

    public static void bind(ServiceBinder binder){
        binder.bind(UserDetailsService.class, UserDetailsServiceImpl.class);
    }

    public static void contributeAlias(Configuration<AliasContribution<PasswordEncoder>> configuration ) {
        configuration.add( AliasContribution.create(PasswordEncoder.class, new ShaPasswordEncoder() ) );
    }

    public static void contributeProviderManager(
            OrderedConfiguration<AuthenticationProvider> configuration,
            @InjectService("DaoAuthenticationProvider") AuthenticationProvider daoAuthenticationProvider) {
        configuration.add("daoAuthenticationProvider", daoAuthenticationProvider);
    }

    // TODO : find a generic solution for this
//    public static void contributeTypeCoercer(Configuration<CoercionTuple<?,?>> configuration) {
//        Coercion<String, UserInfo> stringToUserId = new Coercion<String, UserInfo>() {
//            @Override
//            public UserInfo coerce(String id) {
//                return new UserInfo(id);
//            }
//        };
//        Coercion<UserInfo, String> userIdToString = new Coercion<UserInfo, String>() {
//            @Override
//            public String coerce(UserInfo id) {
//                return id.getUsername();
//            }
//        };
//
//        configuration.add(new CoercionTuple<String, UserInfo>(String.class, UserInfo.class, stringToUserId));
//        configuration.add(new CoercionTuple<UserInfo, String>(UserInfo.class, String.class, userIdToString));
//    }

    public static void contributeValueEncoderSource(
            MappedConfiguration<Class<?>, ValueEncoderFactory<?>> configuration,
            final com.mysema.rdfbean.object.Configuration rdfBeanConfiguration,
            final SessionFactory sessionFactory){

        // TODO : move to RDFBeanModule
        for (final Class cl : Arrays.asList(DocumentNote.class, Document.class, Note.class)){
            configuration.add(cl, new ValueEncoderFactory(){
                final MappedProperty idProperty = rdfBeanConfiguration.getMappedClass(cl).getIdProperty();
                @Override
                public ValueEncoder create(Class type) {
                    return new ValueEncoder(){
                        @Override
                        public String toClient(Object value) {
                            // TODO : handle other id types as well
                            return idProperty.getValue(new BeanMap(value)).toString();
                        }
                        @Override
                        public Object toValue(String id) {
                            Session session = sessionFactory.getCurrentSession();
                            boolean close = session == null;
                            if (session == null){
                                session = sessionFactory.openSession();
                            }
                            try{
                                return session.getById(id, cl);
                            }finally{
                                if (close){
                                    try {
                                        session.close();
                                    } catch (IOException e) {
                                        throw new RepositoryException(e);
                                    }
                                }
                            }
                        }
                    };
                }

            });
        }

        configuration.add(UserInfo.class, new ValueEncoderFactory<UserInfo>(){
            @Override
            public ValueEncoder<UserInfo> create(Class<UserInfo> type) {
                return new ValueEncoder<UserInfo>(){
                    @Override
                    public String toClient(UserInfo value) {
                        return value.getUsername();
                    }
                    @Override
                    public UserInfo toValue(String clientValue) {
                        return new UserInfo(clientValue);
                    }
                };
            }

        });
    }


}
