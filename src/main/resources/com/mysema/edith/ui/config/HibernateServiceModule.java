package com.mysema.edith.ui.config;

import com.mysema.edith.services.*;

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

    public static void contributeHibernateEntityPackageManager(Configuration<String> configuration) {
        configuration.add("fi.finlit.edith.sql.domain");
    }

    private HibernateServiceModule() {
    }
}