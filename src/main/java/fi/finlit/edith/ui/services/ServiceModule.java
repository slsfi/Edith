/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;

import com.mysema.rdfbean.Namespaces;
import com.mysema.rdfbean.model.FetchStrategy;
import com.mysema.rdfbean.model.PredicateWildcardFetch;
import com.mysema.rdfbean.model.Repository;
import com.mysema.rdfbean.model.io.Format;
import com.mysema.rdfbean.model.io.RDFSource;
import com.mysema.rdfbean.object.Configuration;
import com.mysema.rdfbean.object.DefaultConfiguration;
import com.mysema.rdfbean.sesame.MemoryRepository;
import com.mysema.rdfbean.tapestry.TransactionalAdvisor;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevisionRepository;
import fi.finlit.edith.domain.TermRepository;
import fi.finlit.edith.domain.UserRepository;
import fi.finlit.edith.ui.services.svn.SubversionService;
import fi.finlit.edith.ui.services.svn.SubversionServiceImpl;

/**
 * ServiceModule provides service bindings and RDFBean configuration elements
 *
 * @author tiwe
 * @version $Id$
 *
 */
public final class ServiceModule {
    private ServiceModule() {}

    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration) throws IOException {
        // app config
//        configuration.add(EDITH.SVN_CACHE_DIR, "${java.io.tmpdir}/svncache");
        Properties properties = new Properties();
        properties.load(AppModule.class.getResourceAsStream("/edith.properties"));
        String projectVersion = properties.getProperty(EDITH.PROJECT_VERSION);
        configuration.add(SymbolConstants.APPLICATION_VERSION, projectVersion != null ? projectVersion : "" + Calendar.getInstance().getTimeInMillis());
        for (Map.Entry<Object, Object> entry : properties.entrySet()){
            configuration.add(entry.getKey().toString(), entry.getValue().toString());
        }
    }

    // TODO : get rid of match
    @Match( { "AdminService", "DocumentRepository", "NoteRepository", "UserRepository",
            "NoteRevisionRepository", "TermRepository" })
    public static void adviseTransactions(TransactionalAdvisor advisor,
            MethodAdviceReceiver receiver) {
        advisor.addTransactionCommitAdvice(receiver);
    }

    public static void bind(ServiceBinder binder){
        binder.bind(AdminService.class, AdminServiceImpl.class);
        binder.bind(DocumentRepository.class, DocumentRepositoryImpl.class);
        binder.bind(NoteRepository.class, NoteRepositoryImpl.class);
        binder.bind(NoteRevisionRepository.class, NoteRevisionRepositoryImpl.class);
        binder.bind(TermRepository.class, TermRepositoryImpl.class);
        binder.bind(UserRepository.class, UserRepositoryImpl.class);
        binder.bind(SubversionService.class, SubversionServiceImpl.class);
        binder.bind(DocumentRenderer.class, DocumentRendererImpl.class);
        binder.bind(AuthService.class, SpringSecurityAuthService.class);
        binder.bind(TimeService.class, SimpleTimeService.class);
    }

    public static Configuration buildConfiguration(){
        DefaultConfiguration configuration = new DefaultConfiguration();
        configuration.setFetchStrategies(Collections.<FetchStrategy>singletonList(new PredicateWildcardFetch()));
        configuration.addPackages(Document.class.getPackage());
        return configuration;
    }

    public static Repository buildRepository(
            @Inject @Symbol(EDITH.RDFBEAN_DATA_DIR) String rdfbeanDataDir,
            RegistryShutdownHub hub) {
        Namespaces.register("edith", EDITH.NS);
        final MemoryRepository repository = new MemoryRepository();
        repository.setDataDirName(rdfbeanDataDir);
        repository.setSources(
            new RDFSource("classpath:/edith.ttl", Format.TURTLE, EDITH.NS)
        );
        hub.addRegistryShutdownListener(new RegistryShutdownListener(){
            @Override
            public void registryDidShutdown() {
                repository.close();
            }
        });
        return repository;
    }
}