package fi.finlit.edith.ui.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.hibernate.HibernateTransactionAdvisor;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Match;

import com.mysema.rdfbean.object.Configuration;
import com.mysema.rdfbean.object.DefaultConfiguration;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.ui.services.hibernate.DocumentDaoImpl;
import fi.finlit.edith.ui.services.svn.SubversionService;
import fi.finlit.edith.ui.services.svn.SubversionServiceImpl;

public final class HibernateServiceModule {
    // public static void adviseTransactions(TransactionalAdvisor advisor,
    // MethodAdviceReceiver receiver) {
    // advisor.addTransactionCommitAdvice(receiver);
    // }

    @Match("*Dao")
    public static void adviseTransactions(HibernateTransactionAdvisor advisor,
            MethodAdviceReceiver receiver) {
        advisor.addTransactionCommitAdvice(receiver);
    }

    public static void bind(ServiceBinder binder) {
        // services
        binder.bind(SubversionService.class, SubversionServiceImpl.class);
        // binder.bind(ContentRenderer.class, ContentRendererImpl.class);
        // binder.bind(AuthService.class, SpringSecurityAuthService.class);
        // binder.bind(TimeService.class, SimpleTimeService.class);
        // binder.bind(TasksService.class);

        // repositories
        // binder.bind(AdminService.class, AdminServiceImpl.class);
        binder.bind(DocumentDao.class, DocumentDaoImpl.class);
        // binder.bind(NoteDao.class, NoteRepositoryImpl.class);
        // binder.bind(DocumentNoteDao.class, DocumentNoteRepositoryImpl.class);
        // binder.bind(TermDao.class, TermRepositoryImpl.class);
        // binder.bind(PersonDao.class, PersonRepositoryImpl.class);
        // binder.bind(PlaceDao.class, PlaceRepositoryImpl.class);
        // binder.bind(UserDao.class, UserRepositoryImpl.class);

        // tasks
        // binder.bind(ReplacedByAdditionTask.class);
    }

    public static Configuration buildConfiguration() {
        DefaultConfiguration configuration = new DefaultConfiguration(EDITH.NS);
        configuration.addPackages(Document.class.getPackage());
        return configuration;
    }

    // public static Repository buildRepository(
    // @Inject @Symbol(EDITH.RDFBEAN_DATA_DIR) String rdfbeanDataDir, RegistryShutdownHub hub) {
    // final NativeRepository repository = new NativeRepository();
    // repository.setIndexes("spoc,posc,opsc");
    // repository.setDataDirName(rdfbeanDataDir);
    //
    // hub.addRegistryShutdownListener(new RegistryShutdownListener() {
    // @Override
    // public void registryDidShutdown() {
    // repository.close();
    // }
    // });
    // return repository;
    // }

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

    private HibernateServiceModule() {
    }
}