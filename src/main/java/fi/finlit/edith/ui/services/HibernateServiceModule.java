package fi.finlit.edith.ui.services;

import java.io.IOException;

import org.apache.tapestry5.hibernate.HibernateTransactionAdvisor;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.ui.services.hibernate.DocumentDaoImpl;
import fi.finlit.edith.ui.services.hibernate.DocumentNoteDaoImpl;
import fi.finlit.edith.ui.services.hibernate.NoteDaoImpl;
import fi.finlit.edith.ui.services.hibernate.PersonDaoImpl;
import fi.finlit.edith.ui.services.hibernate.PlaceDaoImpl;
import fi.finlit.edith.ui.services.hibernate.TermDaoImpl;
import fi.finlit.edith.ui.services.hibernate.UserDaoImpl;
import fi.finlit.edith.ui.services.svn.SubversionService;

public final class HibernateServiceModule {
    
    @Match("*Dao")
    public static void adviseTransactions(HibernateTransactionAdvisor advisor,
            MethodAdviceReceiver receiver) {
        advisor.addTransactionCommitAdvice(receiver);
    }

    public static void bind(ServiceBinder binder) {
        binder.bind(UserDao.class, UserDaoImpl.class);
        binder.bind(NoteDao.class, NoteDaoImpl.class);
        binder.bind(DocumentDao.class, DocumentDaoImpl.class);
        binder.bind(DocumentNoteDao.class, DocumentNoteDaoImpl.class);
        binder.bind(PersonDao.class, PersonDaoImpl.class);
        binder.bind(PlaceDao.class, PlaceDaoImpl.class);
        binder.bind(TermDao.class, TermDaoImpl.class);
    }

    public static void contributeHibernateEntityPackageManager(Configuration<String> configuration)
    {
        configuration.add("fi.finlit.edith.sql.domain");
    }
    
    private HibernateServiceModule() {
    }
}