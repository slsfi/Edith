/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;

import com.mysema.rdfbean.model.Repository;
import com.mysema.rdfbean.model.io.Format;
import com.mysema.rdfbean.model.io.RDFSource;
import com.mysema.rdfbean.object.Configuration;
import com.mysema.rdfbean.object.DefaultConfiguration;
import com.mysema.rdfbean.object.identity.IdentityService;
import com.mysema.rdfbean.sesame.MemoryRepository;
import com.mysema.rdfbean.tapestry.TransactionalAdvisor;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevisionRepository;
import fi.finlit.edith.domain.UserRepository;

/**
 * ServiceModule provides service bindings and RDFBean configuration elements
 *
 * @author tiwe
 * @version $Id$
 *
 */
public class ServiceModule {
    
    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration) throws IOException {        
        // app config
        configuration.add(EDITH.SVN_CACHE_DIR, "${java.io.tmpdir}/svncache");
        Properties properties = new Properties();
        properties.load(AppModule.class.getResourceAsStream("/edith.properties"));
        for (Map.Entry<Object, Object> entry : properties.entrySet()){
            configuration.add(entry.getKey().toString(), entry.getValue().toString());
        }
    }    
    
    // TODO : get rid of match
    @Match({"DocumentRepository", "NoteRepository", "UserRepository", "NoteRevisionRepository"})
    public static void adviseTransactions(TransactionalAdvisor advisor, MethodAdviceReceiver receiver){
        advisor.addTransactionCommitAdvice(receiver);
    }
    
    public static void bind(ServiceBinder binder){
        binder.bind(DocumentRepository.class, DocumentRepositoryImpl.class);
        binder.bind(NoteRepository.class, NoteRepositoryImpl.class);
        binder.bind(NoteRevisionRepository.class, NoteRevisionRepositoryImpl.class);
        binder.bind(UserRepository.class, UserRepositoryImpl.class);
        
        binder.bind(DocumentRenderer.class, DocumentRendererImpl.class);
        binder.bind(AuthService.class, SpringSecurityAuthService.class);
    }
    
    public static Configuration buildConfiguration(IdentityService identityService){
        DefaultConfiguration configuration = new DefaultConfiguration();
        configuration.addPackages(Document.class.getPackage());
        configuration.setIdentityService(identityService);
        return configuration;
    }

    // TODO : override identityService
    
    public static Repository buildRepository(Configuration configuration) {
        MemoryRepository repository = new MemoryRepository();
        repository.setSources(  
            new RDFSource("classpath:/edith.ttl", Format.TURTLE, EDITH.NS)
        );                
        return repository;        
    }
    
    public static SVNClientManager buildSVNClientManager(){
        return SVNClientManager.newInstance();
    }   
    
    public static SVNRepository buildSVNRepository(
            @Inject @Symbol(EDITH.REPO_URL_PROPERTY) String repoURL) throws SVNException{
        return SVNRepositoryFactory.create(SVNURL.parseURIEncoded(repoURL));
    }
    
    
}