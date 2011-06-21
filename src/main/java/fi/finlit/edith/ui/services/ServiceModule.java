/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysema.rdfbean.model.Repository;
import com.mysema.rdfbean.object.Configuration;
import com.mysema.rdfbean.object.DefaultConfiguration;
import com.mysema.rdfbean.sesame.NativeRepository;
import com.mysema.rdfbean.tapestry.TransactionalAdvisor;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.ui.services.content.ContentRenderer;
import fi.finlit.edith.ui.services.content.ContentRendererImpl;
import fi.finlit.edith.ui.services.hibernate.DocumentDaoImpl;
import fi.finlit.edith.ui.services.hibernate.UserDaoImpl;
import fi.finlit.edith.ui.services.repository.AdminServiceImpl;
import fi.finlit.edith.ui.services.repository.DocumentNoteRepositoryImpl;
import fi.finlit.edith.ui.services.repository.DocumentRepositoryImpl;
import fi.finlit.edith.ui.services.repository.NoteRepositoryImpl;
import fi.finlit.edith.ui.services.repository.PersonRepositoryImpl;
import fi.finlit.edith.ui.services.repository.PlaceRepositoryImpl;
import fi.finlit.edith.ui.services.repository.TermRepositoryImpl;
import fi.finlit.edith.ui.services.repository.UserRepositoryImpl;
import fi.finlit.edith.ui.services.svn.SubversionService;
import fi.finlit.edith.ui.services.svn.SubversionServiceImpl;
import fi.finlit.edith.ui.services.tasks.ReplacedByAdditionTask;
import fi.finlit.edith.ui.services.tasks.TasksService;

/**
 * ServiceModule provides service bindings and RDFBean configuration elements
 *
 * @author tiwe
 * @version $Id$
 *
 */
public final class ServiceModule {

    private static final Logger logger = LoggerFactory.getLogger(HibernateDataModule.class);

    public static void bind(ServiceBinder binder) {
        // services
        binder.bind(SubversionService.class, SubversionServiceImpl.class);
        binder.bind(ContentRenderer.class, ContentRendererImpl.class);
        binder.bind(AuthService.class, SpringSecurityAuthService.class);

        /*
        binder.bind(TimeService.class, SimpleTimeService.class);
//        binder.bind(TasksService.class);

        // repositories
        binder.bind(AdminService.class, AdminServiceImpl.class);
        binder.bind(DocumentRepository.class, DocumentRepositoryImpl.class);
        binder.bind(NoteRepository.class, NoteRepositoryImpl.class);
        binder.bind(DocumentNoteRepository.class, DocumentNoteRepositoryImpl.class);
        binder.bind(TermRepository.class, TermRepositoryImpl.class);
        binder.bind(PersonRepository.class, PersonRepositoryImpl.class);
        binder.bind(PlaceRepository.class, PlaceRepositoryImpl.class);
        binder.bind(UserRepository.class, UserRepositoryImpl.class);

        // tasks
        binder.bind(ReplacedByAdditionTask.class);
        */
    }

    @Startup
    public static void initData(SubversionService subversionService)
            throws IOException {
        logger.info("Starting up subversion");
        subversionService.initialize();
    }

    public static Configuration buildConfiguration() {
        DefaultConfiguration configuration = new DefaultConfiguration(EDITH.NS);
        configuration.addPackages(Document.class.getPackage());
        return configuration;
    }

    public static Repository buildRepository(
            @Inject @Symbol(EDITH.RDFBEAN_DATA_DIR) String rdfbeanDataDir, RegistryShutdownHub hub) {
        final NativeRepository repository = new NativeRepository();
        repository.setIndexes("spoc,posc,opsc");
        repository.setDataDirName(rdfbeanDataDir);


        hub.addRegistryShutdownListener(new RegistryShutdownListener() {
            @Override
            public void registryDidShutdown() {
                repository.close();
            }
        });
        return repository;
    }

    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration) throws IOException {
        // app config
        Properties properties = new Properties();

        InputStream stream = null;
        try {
            stream = AppModule.class.getResourceAsStream("/edith.properties");
            properties.load(stream);
            if (properties.getProperty(SymbolConstants.APPLICATION_VERSION) == null) {
                configuration.add(SymbolConstants.APPLICATION_VERSION,
                        String.valueOf(Calendar.getInstance().getTimeInMillis()));
            }
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                configuration.add(entry.getKey().toString(), entry.getValue().toString());
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private ServiceModule() {
    }
}