package fi.finlit.edith.ui.services;

import org.apache.tapestry5.hibernate.HibernateTransactionAdvisor;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Match;

import fi.finlit.edith.ui.services.hibernate.UserDaoImpl;

public final class HibernateServiceModule {
    @Match("*Dao")
    public static void adviseTransactions(HibernateTransactionAdvisor advisor,
            MethodAdviceReceiver receiver) {
        advisor.addTransactionCommitAdvice(receiver);
    }

    public static void bind(ServiceBinder binder) {
        binder.bind(UserDao.class, UserDaoImpl.class);
    }

    private HibernateServiceModule() {
    }
}