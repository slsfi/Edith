/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.editio.ui.services;

import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.openrdf.rio.RDFFormat;

import com.mysema.rdfbean.model.Repository;
import com.mysema.rdfbean.object.Configuration;
import com.mysema.rdfbean.object.DefaultConfiguration;
import com.mysema.rdfbean.object.identity.IdentityService;
import com.mysema.rdfbean.sesame.MemoryRepository;
import com.mysema.rdfbean.sesame.RDFSource;
import com.mysema.rdfbean.tapestry.RDFBeanModule;
import com.mysema.rdfbean.tapestry.TransactionalAdvisor;
import com.mysema.tapestry.core.CallbackService;

import fi.finlit.editio.EDITIO;
import fi.finlit.editio.domain.Document;
import fi.finlit.editio.domain.UserRepository;

@SubModule(RDFBeanModule.class)
public class ServiceModule {
    
    @Match({"UserRepository", "CallbackService"})
    public static void adviseTransactions(TransactionalAdvisor advisor, MethodAdviceReceiver receiver){
        advisor.addTransactionCommitAdvice(receiver);
    }
    
    public static void bind(ServiceBinder binder){
//        binder.bind(ArticleRepository.class, ArticleRepositoryImpl.class);
//      binder.bind(FeedGenerator.class, FeedGeneratorImpl.class);
        binder.bind(UserRepository.class, UserRepositoryImpl.class);  
        binder.bind(CallbackService.class, CallbackServiceImpl.class);
        binder.bind(AuthService.class, SpringSecurityAuthService.class);
    }

    public static Configuration buildConfiguration(IdentityService identityService){
        DefaultConfiguration configuration = new DefaultConfiguration();
        configuration.addPackages(Document.class.getPackage());
        configuration.setIdentityService(identityService);
        return configuration;
    }
    
    public static Repository buildRepository(Configuration configuration) {
        MemoryRepository repository = new MemoryRepository();
        repository.setSources(  
            new RDFSource("classpath:/editio.owl", RDFFormat.RDFXML, EDITIO.NS),
            new RDFSource("classpath:/base.ttl", RDFFormat.TURTLE, EDITIO.DATA),
            new RDFSource("classpath:/data.ttl", RDFFormat.TURTLE, EDITIO.DATA)
        );                
        return repository;        
    }   
    
}